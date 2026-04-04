#version 150

uniform float AnimTime;

in vec2 localPos;
in vec4 vertexColor;
out vec4 fragColor;

mat2 rot2d(float a) {
    float s = sin(a), c = cos(a);
    return mat2(c, -s, s, c);
}

float hash12(vec2 p) {
    vec3 p3  = fract(vec3(p.xyx) * .1031);
    p3 += dot(p3, p3.yzx + 33.33);
    return fract((p3.x + p3.y) * p3.z);
}

float noise2D(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(hash12(i), hash12(i + vec2(1.0, 0.0)), f.x),
    mix(hash12(i + vec2(0.0, 1.0)), hash12(i + vec2(1.0, 1.0)), f.x), f.y);
}

float fbm2D(vec2 p) {
    float v = 0.0, a = 0.5;
    for (int i = 0; i < 4; i++) {
        v += a * noise2D(p); p *= 2.0; a *= 0.5;
    }
    return v;
}

float hash(vec3 p) {
    p  = fract(p * .1031);
    p += dot(p, p.zyx + 31.32);
    return fract((p.x + p.y) * p.z);
}

float noise3D(vec3 p) {
    vec3 i = floor(p);
    vec3 f = fract(p);
    f = f * f * (3.0 - 2.0 * f);
    return mix(mix(mix( hash(i + vec3(0,0,0)), hash(i + vec3(1,0,0)), f.x),
    mix( hash(i + vec3(0,1,0)), hash(i + vec3(1,1,0)), f.x), f.y),
    mix(mix( hash(i + vec3(0,0,1)), hash(i + vec3(1,0,1)), f.x),
    mix( hash(i + vec3(0,1,1)), hash(i + vec3(1,1,1)), f.x), f.y), f.z);
}

float fbm3D(vec3 p) {
    float v = 0.0, a = 0.5;
    for (int i = 0; i < 4; i++) {
        v += a * noise3D(p); p *= 2.0; a *= 0.5;
    }
    return v;
}

vec3 calculateBloom(vec2 uv, float r0, float time, float voidRadius) {
    float falloff = exp(-(r0 - voidRadius) * 2.8);
    falloff = max(0.0, falloff);

    float shimmer = fbm2D(uv * 1.5 - time * 0.1);
    falloff *= 0.6 + 0.4 * shimmer;

    vec3 bloomCol = mix(vec3(0.5, 0.0, 0.0), vec3(1.0, 0.3, 0.0), falloff);
    float coreMask = smoothstep(voidRadius - 0.05, voidRadius + 0.1, r0);

    return bloomCol * falloff * coreMask * 1.5;
}

void main() {
    vec2 uv = localPos * 1.3;

    float r0 = length(uv);
    float angle = atan(uv.y, uv.x);

    float shapeDistortion = fbm2D(vec2(angle * 2.0, AnimTime * 0.15)) * 0.5;
    float rShape = r0 - shapeDistortion;

    vec2 wobble = vec2(
    fbm2D(uv * 2.0 + AnimTime * 0.1),
    fbm2D(uv * 2.0 - AnimTime * 0.1 + 50.0)
    ) * 2.0 - 1.0;

    vec2 warpedUV = uv;
    warpedUV *= rot2d(r0 * 0.8 - AnimTime * 0.25);
    warpedUV += wobble * 0.2 * pow(r0, 0.8);

    float a = atan(warpedUV.y, warpedUV.x);
    float r = length(warpedUV);

    float surge = fbm2D(vec2(a * 1.5, AnimTime * 0.4)) * 1.5;

    float fakeZ = sqrt(max(0.0, 1.0 - r0 * r0));


    vec3 cyl = vec3(cos(a) * 2.0, sin(a) * 2.0, r * 4.0 - AnimTime * 1.8 + surge - fakeZ * 2.5);

    float filamentNoise = fbm3D(cyl);
    float serpents = 1.0 - abs(filamentNoise - 0.5) * 2.0;

    serpents = pow(serpents, 2.2 + r0 * 1.5);

    serpents *= fbm3D(cyl * 1.2 - vec3(0.0, 0.0, AnimTime * 0.3)) * 1.6;

    float edgeErosion = smoothstep(0.4, 1.2, r0) * 0.35;
    serpents -= edgeErosion;
    serpents = max(0.0, serpents);

    float spikeNoise = fbm2D(vec2(angle * 10.0, AnimTime * 1.5));
    float voidRadius = 0.18 + spikeNoise * 0.12;
    float voidCoreMask = smoothstep(voidRadius - 0.05, voidRadius + 0.05, r0);

    float spikeGlow = smoothstep(voidRadius + 0.1, voidRadius - 0.02, r0) * spikeNoise * 2.0;

    vec3 colBlack = vec3(0.0);
    vec3 colYellow = vec3(1.0, 0.95, 0.1);
    vec3 colOrange = vec3(1.0, 0.4, 0.0);
    vec3 colRed = vec3(0.8, 0.02, 0.0);

    float colorNoise = fbm2D(uv * 3.0 - AnimTime * 0.2);
    float colorDist = r0;

    if (r0 > 0.4) {
        colorDist += (colorNoise - 0.5) * 0.6 * smoothstep(0.4, 0.9, r0);
    }

    vec3 baseColor;

    if (r0 < 0.4) {
        baseColor = mix(colBlack, colYellow, smoothstep(voidRadius - 0.05, 0.4, r0));
    } else if (colorDist < 0.7) {
        baseColor = mix(colYellow, colOrange, smoothstep(0.4, 0.7, colorDist));
    } else {
        baseColor = mix(colOrange, colRed, smoothstep(0.7, 1.2, colorDist));
    }

    baseColor += colYellow * spikeGlow * 1.5;

    float coreFactor = pow(serpents, 1.2);
    float activeCoreHeat = coreFactor * max(0.0, 1.0 - r0 * 1.5);
    vec3 hotWhite = vec3(1.0, 1.0, 0.8);
    baseColor = mix(baseColor, hotWhite, activeCoreHeat);

    float voidEdgeHeat = smoothstep(voidRadius + 0.15, voidRadius, r0) * voidCoreMask;
    float heatAlpha = pow(voidEdgeHeat, 2.0);

    baseColor += hotWhite * heatAlpha * 2.5;

    float edgeMask = smoothstep(1.1, 0.5, rShape);

    float filamentAlpha = serpents * voidCoreMask * edgeMask;
    filamentAlpha += spikeGlow * voidCoreMask * 0.8 * edgeMask;

    filamentAlpha += heatAlpha;

    vec3 filamentEmission = baseColor * 2.5 * filamentAlpha;

    vec3 backgroundBloom = calculateBloom(uv, r0, AnimTime, voidRadius);

    vec3 finalEmission = backgroundBloom + filamentEmission;

    float voidBlocking = smoothstep(voidRadius + 0.02, voidRadius - 0.05, r0);


    float whiteHotFactor = clamp(heatAlpha + activeCoreHeat, 0.0, 1.0);
    vec3 activeTint = mix(vertexColor.xyz, vec3(1.0), whiteHotFactor);

    vec3 preMultipliedRGB = finalEmission * activeTint * vertexColor.a;
    float preMultipliedAlpha = voidBlocking * vertexColor.a;

    if (length(preMultipliedRGB) <= 0.01 && preMultipliedAlpha <= 0.01) {
        discard;
    }

    fragColor = vec4(preMultipliedRGB, preMultipliedAlpha);
}