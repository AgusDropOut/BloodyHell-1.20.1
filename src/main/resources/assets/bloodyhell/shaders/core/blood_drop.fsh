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

out vec4 fragColor;

float map(vec3 localP) {
    float baseRadius = mix(0.1, 0.55, chargeLevel);
    float core = length(localP) - baseRadius;

    float freq = mix(5.0, 18.0, stabilization);
    float amp = mix(0.15, 0.03, stabilization);

    float displacement = sin(localP.x * freq + u_Time * 2.0) *
    sin(localP.y * freq + u_Time * 1.5) *
    sin(localP.z * freq + u_Time * 1.2) * amp;

    float throbFreq = mix(3.0, 12.0, stabilization);
    float throbAmp = mix(0.05, 0.015, stabilization);
    float throb = sin(u_Time * throbFreq) * throbAmp;

    return core + displacement - throb;
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

    bool hit = false;
    vec3 p;

    for(int i = 0; i < 32; i++) {

        p = ro + rayDir * t;

        if(length(p - relativeBlobCenter) > 1.55) break;

        vec4 viewSpaceHit = u_VanillaModelViewMat * vec4(p + u_BobbingOffset, 1.0);
        vec4 projP = u_ProjMat * viewSpaceHit;

        if (projP.w > 0.0) {
            float currentDepth = (projP.z / projP.w) * 0.5 + 0.5;

            if (sceneDepth < currentDepth) {
                break;
            }
        }

        float d = map(p - relativeBlobCenter);

        if(d < 0.005) {
            hit = true;
            break;
        }
        t += d * 0.8;
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

        fragColor = vec4(finalColor, 0.95);
    } else {
        discard;
    }
}