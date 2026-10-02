#version 150

in vec3 v_ViewPos;
in vec2 v_ScreenUV;

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform mat4 u_InvProjMat;
uniform vec3 u_OrbViewPos;
uniform mat3 u_OrbRotation;
uniform float u_Time;

out vec4 fragColor;


float map(vec3 p, float time) {
    float t = time * 1.5;
    float pulse = pow(sin(fract(t) * 3.1415), 20.0) * 0.06 +
    pow(sin(fract(t + 0.15) * 3.1415), 20.0) * 0.03;
    float baseRadius = 0.25;

    float angle = atan(p.x, p.z);
    float grooves = sin(angle * 6.0) * 0.03;

    return length(p) - (baseRadius + pulse + grooves);
}


float opTwist(vec3 p, float time ) {
    const float k = 5.0;
    float twistAngle = k * p.y + (time * 1.5);
    float c = cos(twistAngle);
    float s = sin(twistAngle);
    mat2 m = mat2(c, -s, s, c);

    vec3 q = p;
    q.xz = m * q.xz;
    return map(q, time);
}


vec3 calcNormal(vec3 p, float time) {
    float eps = 0.001;
    vec2 e = vec2(1.0, -1.0) * eps;
    return normalize(
    e.xyy * opTwist(p + e.xyy, time) +
    e.yyx * opTwist(p + e.yyx, time) +
    e.yxy * opTwist(p + e.yxy, time) +
    e.xxx * opTwist(p + e.xxx, time)
    );
}

void main() {
    vec3 ro = vec3(0.0);
    vec3 rd = normalize(v_ViewPos);


    float distToQuad = length(v_ViewPos);
    float t = max(0.0, distToQuad - 0.5);


    float sceneDepth = texture(Sampler1, v_ScreenUV).r;
    vec2 ndc = v_ScreenUV * 2.0 - 1.0;
    vec4 sceneClip = vec4(ndc, sceneDepth * 2.0 - 1.0, 1.0);
    vec4 sceneView = u_InvProjMat * sceneClip;
    sceneView /= sceneView.w;
    float maxT = length(sceneView.xyz);

    bool hit = false;
    vec3 p;
    mat3 invOrbRot = transpose(u_OrbRotation);

    for(int i = 0; i < 50; i++) {
        p = ro + rd * t;

        if (t > maxT - 0.05) { break; }


        vec3 localP = p - u_OrbViewPos;


        vec3 alignedP = invOrbRot * localP;


        float d = opTwist(alignedP, u_Time);

        if(d < 0.002) {
            hit = true;
            break;
        }

        if (t > distToQuad + 0.5) { break; }

        t += d * 0.7;
    }

    if(hit) {
        vec3 localP = p - u_OrbViewPos;
        vec3 alignedP = invOrbRot * localP;
        vec3 nLocal = calcNormal(alignedP, u_Time);


        vec2 distortedUV = v_ScreenUV + nLocal.xy * 0.15;


        vec3 distortedBg = texture(Sampler0, distortedUV).rgb;
        float luma = dot(distortedBg, vec3(0.299, 0.587, 0.114));

        vec3 anomalyColor;
        if (luma > 0.45) {
            anomalyColor = vec3(1.0, 0.0, 0.0);
        } else {
            anomalyColor = vec3(0.0, 0.0, 0.0);
        }

        vec3 lightDir = normalize(vec3(0.5, 1.0, 0.8));
        float diff = max(dot(nLocal, lightDir), 0.0);
        float fresnel = pow(1.0 - max(dot(nLocal, -rd), 0.0), 3.0);

        vec3 baseVolume = vec3(0.08, 0.0, 0.0);
        vec3 edgeGlow = vec3(1.0, 0.1, 0.1);

        vec3 finalColor = anomalyColor + (baseVolume * diff) + (edgeGlow * fresnel * 0.8);

        fragColor = vec4(finalColor, 1.0);
    } else {
        discard;
    }
}