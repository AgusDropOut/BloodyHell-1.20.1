#version 150

uniform float TimeAnim;
uniform sampler2D Sampler0;
// Eliminamos Sampler2 de acá porque no se usa y tiraba un warning inofensivo en la consola.

in vec2 texCoord0;
out vec4 fragColor;

#define MAX_STEPS 64
#define STEP_SCALE 0.10
#define MIN_STEP 0.03
#define MAX_DEPTH 16.0

#define PAGE_UV_SCALE 4.2
#define PARALLAX_TILT 0.95
#define AUTO_SWAY_SPEED 0.65
#define VORTEX_TWIST 0.22

#define WEB_SCALE 2.4
#define WEB_SPEED 0.18
#define WARP_STRENGTH_1 1.25
#define WARP_STRENGTH_2 0.65
#define VEIN_SHARPNESS 3.0

#define NODE_SCALE 7.5
#define NODE_POWER 18.0
#define NODE_INTENSITY 5.0

#define STAR_3D_SCALE 3.5
#define STAR_3D_DENSITY 0.74
#define STAR_3D_SIZE 0.08
#define STAR_3D_BRIGHTNESS 3.8
#define BG_STAR_SCALE 65.0
#define BG_STAR_DENSITY 0.92

#define FRAME_ROUNDNESS 0.25
#define FRAME_START 0.62
#define FRAME_END 0.94
#define FRAME_NOISE_SCALE 6.5
#define FRAME_NOISE_SPEED 0.25
#define FRAME_NOISE_AMP 0.18
#define FRAME_RIM_GLOW 0.45

#define STEP_ALPHA 0.042
#define VOID_ABSORPTION 5.5
#define DEPTH_FOG 0.17
#define EMISSION_BOOST 2.1

const vec3 COL_VOID_BG    = vec3(0.01, 0.015, 0.005);
const vec3 COL_FRAME_EDGE = vec3(0.11, 0.09, 0.04);
const vec3 COL_FRAME_DARK = vec3(0.02, 0.025, 0.01);
const vec3 COL_PALE_GREEN = vec3(0.22, 0.34, 0.09);
const vec3 COL_PALE_GOLD  = vec3(0.65, 0.55, 0.15);
const vec3 COL_SOFT_CORE  = vec3(0.85, 0.75, 0.30);
const vec3 COL_STAR_TINT  = vec3(0.95, 0.90, 0.65);

mat2 rot2D(float angle) {
    float s = sin(angle); float c = cos(angle);
    return mat2(c, -s, s, c);
}

float hash21(vec2 p) {
    p = fract(p * vec2(123.34, 456.21)); p += dot(p, p + 45.32);
    return fract(p.x * p.y);
}

vec3 hash33(vec3 p3) {
    p3 = fract(p3 * vec3(0.1031, 0.1030, 0.0973)); p3 += dot(p3, p3.yxz + 33.33);
    return fract((p3.xxy + p3.yxx) * p3.zyx);
}

float noise2D(vec2 p) {
    vec2 i = floor(p); vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);

    return mix(mix(hash21(i + vec2(0.0, 0.0)), hash21(i + vec2(1.0, 0.0)), u.x),
    mix(hash21(i + vec2(0.0, 1.0)), hash21(i + vec2(1.0, 1.0)), u.x), u.y);
}

float fbm2D(vec2 p) {
    float v = 0.0; float a = 0.5; mat2 m = rot2D(0.5);
    for (int i = 0; i < 4; i++) {
        v += a * noise2D(p); p = m * p * 2.05; a *= 0.5;
    }
    return v;
}

float eval3DStars(vec3 p) {
    vec3 sp = p * STAR_3D_SCALE; vec3 id = floor(sp); vec3 gv = fract(sp) - 0.5;
    vec3 rnd = hash33(id);
    if (rnd.x < STAR_3D_DENSITY) return 0.0;
    vec3 starPos = (rnd - 0.5) * 0.65;
    float d = length(gv - starPos);
    float twinkle = 0.6 + 0.4 * sin(TimeAnim * (2.5 + rnd.y * 4.0) + rnd.z * 6.2831);
    float star = smoothstep(STAR_3D_SIZE, 0.0, d);
    return pow(star, 2.5) * twinkle * STAR_3D_BRIGHTNESS;
}

float evalBgStars(vec2 uvDir) {
    vec2 sp = uvDir * BG_STAR_SCALE; vec2 id = floor(sp); vec2 gv = fract(sp) - 0.5;
    float rnd = hash21(id);
    if (rnd < BG_STAR_DENSITY) return 0.0;
    vec2 offset = vec2(fract(rnd * 34.56), fract(rnd * 78.91)) - 0.5;
    float d = length(gv - offset * 0.7);
    float twinkle = 0.5 + 0.5 * sin(TimeAnim * 3.0 + rnd * 12.0);
    return smoothstep(0.12, 0.0, d) * twinkle;
}

vec2 getLocalPageUV(vec2 atlasUV) {
    // Restaurado a la matemática precisa original para evitar que el shader dibuje el borde oscuro
    vec2 px = atlasUV * 128.0;
    vec2 local = vec2(0.0);
    if (px.y < 36.0) {
        if (px.x >= 77.0) {
            local.x = (px.x - 77.0) / 13.0;
        } else {
            local.x = (px.x - 61.0) / 13.0;
        }
        local.y = mod(px.y, 17.0) / 16.0;
    } else {
        if (px.x >= 75.0) {
            local.x = (px.x - 75.0) / 13.0;
        } else {
            local.x = (px.x - 59.0) / 13.0;
        }
        local.y = mod(px.y - 40.0, 17.0) / 16.0;
    }
    return clamp(local, 0.0, 1.0) * 2.0 - 1.0;
}

void main() {
    vec2 pageUv = getLocalPageUV(texCoord0);

    vec2 viewTilt = vec2(
    sin(TimeAnim * AUTO_SWAY_SPEED),
    cos(TimeAnim * AUTO_SWAY_SPEED + 0.75)
    ) * PARALLAX_TILT;

    vec3 ro = vec3(pageUv * PAGE_UV_SCALE, 0.0);
    vec3 rd = normalize(vec3(viewTilt, -1.2));

    vec4 accum = vec4(0.0);
    float t = 0.0;

    for (int s = 0; s < MAX_STEPS; s++) {
        vec3 p = ro + rd * t;
        p.xy *= rot2D(t * VORTEX_TWIST);

        vec3 q = p * WEB_SCALE;
        q.z += TimeAnim * WEB_SPEED;
        q += sin(q.zxy * 1.3 + TimeAnim * 0.22) * WARP_STRENGTH_1;
        q += sin(q.yzx * 2.4 + TimeAnim * 0.15) * WARP_STRENGTH_2;

        float cosmicWeb = dot(sin(q), cos(q.zxy));
        float darkTendrils = smoothstep(0.0, 0.35, abs(cosmicWeb));
        cosmicWeb = abs(cosmicWeb) - 0.28 * abs(dot(sin(q * 2.3), cos(q.zxy * 2.3)));

        vec3 sq = p * NODE_SCALE + sin(p.zxy * 3.0 + TimeAnim * 0.4);
        float nodes = abs(sin(sq.x) * cos(sq.y) * sin(sq.z));
        float nodeMask = smoothstep(0.15, 0.85, sin(p.x * 1.4 + p.z) * cos(p.y * 1.4 + p.z));

        nodes = pow(nodes, NODE_POWER) * nodeMask * NODE_INTENSITY;

        float starField3D = eval3DStars(p);

        float veins = pow(clamp(cosmicWeb, 0.0, 1.0), VEIN_SHARPNESS);
        float pulse = 0.8 + 0.2 * sin(TimeAnim * 1.4 - t * 0.9);
        float emission = smoothstep(0.22 * pulse, 0.95, veins) + nodes;

        vec3 baseColor = mix(COL_PALE_GREEN, COL_PALE_GOLD, smoothstep(0.0, 0.55, veins));
        baseColor = mix(baseColor, COL_SOFT_CORE, smoothstep(0.55, 1.0, veins + nodes * 0.3));

        float depthAtten = exp(-t * DEPTH_FOG);
        vec3 stepColor = (baseColor * emission * EMISSION_BOOST + COL_STAR_TINT * starField3D) * depthAtten;

        float darkVeinOcclusion = (1.0 - darkTendrils) * 2.2;
        float voidDensity = STEP_ALPHA + (1.0 - clamp(emission + starField3D, 0.0, 1.0)) * STEP_ALPHA * (VOID_ABSORPTION + darkVeinOcclusion);
        float weight = (1.0 - accum.w) * voidDensity;

        accum.rgb += stepColor * (1.0 - accum.w) * STEP_ALPHA * 2.8;
        accum.w += weight;

        if (accum.w >= 0.99 || t > MAX_DEPTH) break;

        float stepDist = max(MIN_STEP, (1.0 - clamp(veins + starField3D * 0.5, 0.0, 0.85)) * STEP_SCALE);
        t += stepDist;
    }

    vec2 bgUv = pageUv + viewTilt * 0.35;
    float distantStars = evalBgStars(bgUv) * 0.6 + evalBgStars(bgUv * 1.7 + 12.3) * 0.4;
    vec3 abyssBackground = COL_VOID_BG + COL_STAR_TINT * distantStars * 0.7;

    vec3 volumeColor = mix(abyssBackground, accum.rgb, clamp(accum.w, 0.0, 1.0));

    float boxEdge = max(abs(pageUv.x), abs(pageUv.y));
    float radialEdge = length(pageUv * vec2(0.92, 0.92));
    float baseEdge = mix(boxEdge, radialEdge, FRAME_ROUNDNESS);

    vec2 noiseCoord = pageUv * FRAME_NOISE_SCALE + vec2(TimeAnim * FRAME_NOISE_SPEED, -TimeAnim * FRAME_NOISE_SPEED * 0.7);
    float borderNoise = (fbm2D(noiseCoord) - 0.5) * 2.0 * FRAME_NOISE_AMP;
    float perturbedEdge = baseEdge + borderNoise;

    float frameMask = 1.0 - smoothstep(FRAME_START, FRAME_END, perturbedEdge);
    float innerRim = smoothstep(FRAME_START - 0.14, FRAME_START + 0.06, perturbedEdge) * frameMask;

    float paperGrain = fbm2D(pageUv * 14.0);
    vec3 frameColor = mix(COL_FRAME_DARK, COL_FRAME_EDGE, smoothstep(FRAME_START, FRAME_END, baseEdge) * (0.6 + 0.4 * paperGrain));

    vec3 rimTinted = mix(volumeColor, volumeColor + COL_PALE_GOLD * FRAME_RIM_GLOW, innerRim * (1.0 - frameMask) * 3.5);
    vec3 finalColor = mix(frameColor, rimTinted, frameMask);

    fragColor = vec4(finalColor, 1.0);
}