#version 150

in vec2 texCoord0;
uniform sampler2D Sampler0;
uniform float TimeAnim;
uniform float BloodLevel;

out vec4 fragColor;

#define WAVE_AMP 0.015
#define SCROLL_SPEED 0.15
#define VEIL_SPEED 0.3
#define VEIL_SCALE 3.5

#define COLOR_DEEP vec3(0.2, 0.0, 0.0)
#define COLOR_CRIMSON vec3(0.7, 0.05, 0.05)
#define COLOR_HIGHLIGHT vec3(1.0, 0.15, 0.15)

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453);
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(mix(hash(i + vec2(0.0,0.0)), hash(i + vec2(1.0,0.0)), u.x),
    mix(hash(i + vec2(0.0,1.0)), hash(i + vec2(1.0,1.0)), u.x), u.y);
}

float fbm(vec2 p) {
    float v = 0.0;
    float a = 0.5;
    mat2 rot = mat2(0.866, -0.5, 0.5, 0.866);
    for (int i = 0; i < 4; i++) {
        v += a * noise(p);
        p = rot * p * 2.0;
        a *= 0.5;
    }
    return v;
}

void main() {
    vec4 baseTex = texture(Sampler0, texCoord0);


    if (baseTex.a > 0.1 && baseTex.r > 0.05 && baseTex.r > baseTex.g * 1.3 && baseTex.r > baseTex.b * 1.3) {

        vec2 uv = texCoord0;
        float invertedY = 1.0 - uv.y;

        float wave = sin(uv.x * 8.0 + TimeAnim * 2.0) * WAVE_AMP;
        wave += sin(uv.x * 15.0 - TimeAnim * 3.0) * (WAVE_AMP * 0.5);


        float distToCenter = distance(uv, vec2(0.5, 0.5));
        float shadowDepth = smoothstep(0.1, 0.5, distToCenter) + ((1.0 - invertedY) * 0.6);

        vec3 bgCenterGlow = vec3(0.25, 0.02, 0.02);
        vec3 bgEdgeShadow = vec3(0.01, 0.0, 0.0);

        vec3 col = mix(bgCenterGlow, bgEdgeShadow, clamp(shadowDepth, 0.0, 1.0));


        vec2 reflectionPos = uv - vec2(0.5, 0.15);
        float reflection = smoothstep(0.25, 0.05, length(reflectionPos * vec2(1.0, 2.0)));
        col += vec3(0.15, 0.05, 0.05) * reflection;


        if (invertedY < BloodLevel + wave + 0.5) {

            vec2 fogUv = uv * VEIL_SCALE;
            fogUv.y -= TimeAnim * SCROLL_SPEED;

            vec2 q = vec2(fbm(fogUv + vec2(0.0,0.0)), fbm(fogUv + vec2(5.2,1.3)));
            vec2 r = vec2(fbm(fogUv + 4.0 * q + vec2(1.7,9.2) + TimeAnim * VEIL_SPEED),
            fbm(fogUv + 4.0 * q + vec2(8.3,2.8) - TimeAnim * VEIL_SPEED));
            float f = fbm(fogUv + 4.0 * r);

            col = mix(COLOR_DEEP, COLOR_CRIMSON, smoothstep(0.1, 0.6, f));
            col = mix(col, COLOR_HIGHLIGHT, smoothstep(0.5, 0.9, f));

            float surfaceGlow = smoothstep(BloodLevel + wave + 0.48, BloodLevel + wave + 0.5, invertedY);
            col += COLOR_HIGHLIGHT * surfaceGlow * 0.5;
        }

        fragColor = vec4(col, baseTex.a);

    } else {
        fragColor = baseTex;
    }
}