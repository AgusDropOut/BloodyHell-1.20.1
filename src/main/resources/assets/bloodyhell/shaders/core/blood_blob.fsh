#version 150

in vec2 v_TexCoords;

uniform mat4 u_VanillaModelViewMat;
uniform mat4 u_CleanInvViewMat;
uniform mat4 u_ProjMat;
uniform mat4 u_InvProjMat;

uniform vec3 relativeBlobCenter;
uniform vec3 u_BobbingOffset;
uniform float u_Time;
uniform sampler2D Sampler0;

uniform vec3 bloodBaseColor;
uniform vec3 bloodGlowColor;
uniform float chargeLevel;
uniform float stabilization;

uniform float u_Explosion;
uniform float u_Spasm;
uniform float u_Pulse;

out vec4 fragColor;

float map(vec3 localP) {
    float baseRadius = mix(0.1, 0.55, chargeLevel);
    baseRadius += u_Explosion * 1.5;


    float spasmEffect = 0.0;
    if (u_Spasm > 0.0) {
        vec3 p = localP * (4.0 + u_Spasm * 2.0);
        float t = u_Time * 4.0;

        p.x += sin(p.y * 1.5 + t) * 0.8;
        p.y += cos(p.z * 1.3 - t * 0.9) * 0.8;
        p.z += sin(p.x * 1.4 + t * 1.1) * 0.8;

        float bulge = sin(p.x) * cos(p.y) * sin(p.z);
        bulge = pow(abs(bulge), 1.5) * sign(bulge);

        float tumorSize = u_Spasm * 0.6 + (u_Pulse * u_Spasm * 0.1);
        spasmEffect = bulge * tumorSize;

        float throbPanic = sin(u_Time * 0.5) * 0.1 * u_Spasm;
        baseRadius += throbPanic + (u_Pulse * 0.15 * u_Spasm);
    }

    float core = length(localP) - (baseRadius + spasmEffect);


    float freq = mix(5.0, 18.0, stabilization);
    float amp = mix(0.15, 0.03, stabilization);


    float expFreq = 25.0 - (u_Explosion * 10.0);
    float expAmp = u_Explosion * 0.8;
    float explosionDisplacement = sin(localP.x * expFreq + u_Time * 20.0) *
    sin(localP.y * expFreq + u_Time * 25.0) *
    sin(localP.z * expFreq + u_Time * 22.0) * expAmp;

    float displacement = sin(localP.x * freq + u_Time * 2.0) *
    sin(localP.y * freq + u_Time * 1.5) *
    sin(localP.z * freq + u_Time * 1.2) * amp;

    float throbFreq = mix(3.0, 12.0, stabilization);
    float throbAmp = mix(0.05, 0.015, stabilization);
    float throb = sin(u_Time * throbFreq) * throbAmp;

    return core + displacement - throb + explosionDisplacement;
}

vec3 calcNormal(vec3 localP) {
    float eps = 0.01;
    vec2 e = vec2(1.0, -1.0) * eps;
    return normalize(
    e.xyy * map(localP + e.xyy) +
    e.yyx * map(localP + e.yyx) +
    e.yxy * map(localP + e.yxy) +
    e.xxx * map(localP + e.xxx)
    );
}

void main() {
    vec2 ndc = v_TexCoords * 2.0 - 1.0;
    vec4 clipPos = vec4(ndc, 1.0, 1.0);
    vec4 viewPos = u_InvProjMat * clipPos;
    viewPos /= viewPos.w;
    vec3 rdView = normalize(viewPos.xyz);
    vec3 rayDir = normalize((u_CleanInvViewMat * vec4(rdView, 0.0)).xyz);
    vec3 ro = vec3(0.0);

    vec3 oc = ro - relativeBlobCenter;
    float b = dot(oc, rayDir);
    float c = dot(oc, oc) - (1.5 * 1.5);
    float h = b*b - c;

    if (h < 0.0) {
        discard;
    }

    float t = -b - sqrt(h);
    t = max(0.0, t);

    float sceneDepth = texture(Sampler0, v_TexCoords).r;
    float ndcDepth = sceneDepth * 2.0 - 1.0;
    vec4 sceneClip = vec4(ndc, ndcDepth, 1.0);
    vec4 sceneView = u_InvProjMat * sceneClip;
    sceneView /= sceneView.w;
    float maxT = length(sceneView.xyz);

    bool hit = false;
    vec3 p;


    for(int i = 0; i < 120; i++) {
        p = ro + rayDir * t;


        if(length(p - relativeBlobCenter) > 1.55 + (u_Explosion * 3.0) + (u_Spasm * 1.5)) break;

        if (t > maxT - 0.05) {
            break;
        }

        float d = map(p - relativeBlobCenter);
        if(d < 0.005) {
            hit = true;
            break;
        }


        float safeStep = mix(0.8, 0.15, u_Spasm);
        t += d * safeStep;
    }

    if(hit) {
        vec3 nLocal = calcNormal(p - relativeBlobCenter);
        vec3 lightDir = normalize(vec3(0.5, 1.0, 0.3));
        float diff = max(dot(nLocal, lightDir), 0.0);
        float fresnel = pow(1.0 - max(dot(nLocal, -rayDir), 0.0), 3.0);

        vec3 finalColor = bloodBaseColor * (diff * 0.8 + 0.2) + (bloodGlowColor * fresnel);

        if (stabilization > 0.9) {
            finalColor += vec3(1.0, 0.9, 0.8) * pow(fresnel, 5.0) * stabilization;
        }

        finalColor += bloodGlowColor * (u_Explosion * 8.0) + vec3(u_Explosion * 1.5);
        float currentAlpha = 0.95 * (1.0 - u_Explosion);

        fragColor = vec4(finalColor, currentAlpha);
    } else {
        discard;
    }
}