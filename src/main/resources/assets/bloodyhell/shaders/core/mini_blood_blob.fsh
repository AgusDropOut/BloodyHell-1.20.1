#version 150

in vec4 v_ScreenPos;

uniform mat4 u_CleanInvViewMat;
uniform mat4 u_InvProjMat;
uniform vec3 relativeClusterCenter;
uniform float u_Time;
uniform float u_BlobCount;

uniform vec3 u_Blob0;
uniform vec3 u_Blob1;
uniform vec3 u_Blob2;
uniform vec3 u_Blob3;
uniform vec3 u_Blob4;
uniform vec3 u_Blob5;
uniform vec3 u_Blob6;
uniform vec3 u_Blob7;

uniform float u_Size0;
uniform float u_Size1;
uniform float u_Size2;
uniform float u_Size3;
uniform float u_Size4;
uniform float u_Size5;
uniform float u_Size6;
uniform float u_Size7;

uniform vec3 u_Color0;
uniform vec3 u_Color1;
uniform vec3 u_Color2;
uniform vec3 u_Color3;
uniform vec3 u_Color4;
uniform vec3 u_Color5;
uniform vec3 u_Color6;
uniform vec3 u_Color7;

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;

out vec4 fragColor;

float smin(float a, float b, float k) {
    float h = clamp(0.5 + 0.5 * (b - a) / k, 0.0, 1.0);
    return mix(b, a, h) - k * h * (1.0 - h);
}

float getBlobDist(int index, vec3 p) {
    vec3 pos = vec3(0.0);
    float size = 0.15;
    if(index == 0) { pos = u_Blob0; size = u_Size0; }
    else if(index == 1) { pos = u_Blob1; size = u_Size1; }
    else if(index == 2) { pos = u_Blob2; size = u_Size2; }
    else if(index == 3) { pos = u_Blob3; size = u_Size3; }
    else if(index == 4) { pos = u_Blob4; size = u_Size4; }
    else if(index == 5) { pos = u_Blob5; size = u_Size5; }
    else if(index == 6) { pos = u_Blob6; size = u_Size6; }
    else if(index == 7) { pos = u_Blob7; size = u_Size7; }

    pos.y += sin(u_Time * 5.0 + float(index)) * 0.05;
    return length(p - pos) - size;
}

vec3 getBlobColor(int index) {
    if(index == 0) return u_Color0;
    else if(index == 1) return u_Color1;
    else if(index == 2) return u_Color2;
    else if(index == 3) return u_Color3;
    else if(index == 4) return u_Color4;
    else if(index == 5) return u_Color5;
    else if(index == 6) return u_Color6;
    else return u_Color7;
}

float map(vec3 localP) {
    float d = 9999.0;
    for(int i = 0; i < 8; i++) {
        if(float(i) >= u_BlobCount) break;
        float sphere = getBlobDist(i, localP);
        if(i == 0) {
            d = sphere;
        } else {
            d = smin(d, sphere, 0.35);
        }
    }
    return d;
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
    vec2 ndc = v_ScreenPos.xy / v_ScreenPos.w;
    vec2 texCoords = ndc * 0.5 + 0.5;
    vec4 clipPos = vec4(ndc, 1.0, 1.0);
    vec4 viewPos = u_InvProjMat * clipPos;
    viewPos /= viewPos.w;
    vec3 rdView = normalize(viewPos.xyz);
    vec3 rayDir = normalize((u_CleanInvViewMat * vec4(rdView, 0.0)).xyz);
    vec3 ro = vec3(0.0);

    vec3 oc = ro - relativeClusterCenter;
    float b = dot(oc, rayDir);
    float c = dot(oc, oc) - (2.5 * 2.5);
    float h = b * b - c;

    if (h < 0.0) discard;

    float t = max(0.0, -b - sqrt(h));

    float sceneDepth = texture(Sampler1, texCoords).r;
    float ndcDepth = sceneDepth * 2.0 - 1.0;
    vec4 sceneClip = vec4(ndc, ndcDepth, 1.0);
    vec4 sceneView = u_InvProjMat * sceneClip;
    sceneView /= sceneView.w;
    float maxT = length(sceneView.xyz);

    bool hit = false;
    vec3 p;
    float closestDist = 9999.0;
    int closestIndex = 0;

    for(int i = 0; i < 60; i++) {
        p = ro + rayDir * t;

        if(length(p - relativeClusterCenter) > 2.5) break;
        if(t > maxT - 0.05) break;

        float d = map(p - relativeClusterCenter);
        if(d < 0.005) {
            hit = true;
            for(int j = 0; j < 8; j++) {
                if(float(j) >= u_BlobCount) break;
                float dist = getBlobDist(j, p - relativeClusterCenter);
                if(dist < closestDist) {
                    closestDist = dist;
                    closestIndex = j;
                }
            }
            break;
        }
        t += d * 0.8;
    }

    if(hit) {
        vec3 nLocal = calcNormal(p - relativeClusterCenter);

        vec2 offset = nLocal.xy * 0.15;
        vec2 targetUV = clamp(texCoords + offset, 0.001, 0.999);

        float distDepth = texture(Sampler1, targetUV).r;
        float distMaxT = 99999.0;

        if (distDepth < 0.9999 && distDepth > 0.0001) {
            vec4 distClip = vec4(targetUV * 2.0 - 1.0, distDepth * 2.0 - 1.0, 1.0);
            vec4 distView = u_InvProjMat * distClip;
            if (abs(distView.w) > 0.0001) {
                distView /= distView.w;
                distMaxT = length(distView.xyz);
            }
        }

        vec2 finalUV = (distMaxT < t) ? texCoords : targetUV;
        vec3 distortedBg = texture(Sampler0, finalUV).rgb;
        float luma = dot(distortedBg, vec3(0.299, 0.587, 0.114));

        vec3 anomalyColor;
        if (luma > 0.45) {
            anomalyColor = vec3(1.0, 0.0, 0.0);
        } else {
            anomalyColor = vec3(0.0, 0.0, 0.0);
        }

        vec3 lightDir = normalize(vec3(0.5, 1.0, 0.3));
        float diff = max(dot(nLocal, lightDir), 0.0);
        float fresnel = pow(1.0 - max(dot(nLocal, -rayDir), 0.0), 3.0);

        vec3 baseColor = getBlobColor(closestIndex);
        vec3 glowColor = min(vec3(1.0), baseColor * 2.0);

        vec3 baseVolume = anomalyColor + baseColor;
        vec3 finalColor = baseVolume * (diff * 0.8 + 0.2) + (glowColor * fresnel);

        fragColor = vec4(finalColor, 0.95);
    } else {
        discard;
    }
}