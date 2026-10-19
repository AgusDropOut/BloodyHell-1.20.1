#version 150

uniform sampler2D Sampler0;
uniform sampler2D Sampler2;
uniform float TimeAnim;
uniform float MorphProgress;

in vec4 vertexColor;
in vec2 texCoord0;
in vec2 texCoord2;

out vec4 fragColor;

const vec3 COL_CHARRED    = vec3(0.02, 0.025, 0.01);
const vec3 COL_DARK_GREEN = vec3(0.10, 0.18, 0.04);
const vec3 COL_DARK_GOLD  = vec3(0.45, 0.35, 0.08);
const vec3 COL_BRIGHT_GOLD= vec3(0.85, 0.75, 0.30);

mat2 rot2D(float angle) {
    float s = sin(angle); float c = cos(angle);
    return mat2(c, -s, s, c);
}

float hash21(vec2 p) {
    p = fract(p * vec2(123.34, 456.21)); p += dot(p, p + 45.32);
    return fract(p.x * p.y);
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

void main() {
    vec4 baseTex = texture(Sampler0, texCoord0);
    if (baseTex.a < 0.1) discard;

    vec2 fireUv = texCoord0 * 14.0;
    fireUv.y -= TimeAnim * 3.8;
    fireUv.x += sin(TimeAnim * 1.5) * 0.6;

    float n = (fbm2D(fireUv) - 0.5) * 0.45;
    float revealValue = texCoord0.y + n;
    float currentProgress = (MorphProgress * 1.5) - 0.25;

    if (revealValue > currentProgress) {
        discard;
    }

    vec4 color = baseTex * vertexColor;
    float diff = currentProgress - revealValue;
    float fireBand = 0.22;

    if (diff < fireBand) {
        float t = diff / fireBand;
        vec3 fireCol;

        if (t < 0.15) {
            float glow = smoothstep(0.15, 0.0, t);
            fireCol = mix(COL_DARK_GOLD, COL_BRIGHT_GOLD, glow);
            fireCol *= (1.0 + glow * 2.5);
        } else if (t < 0.55) {
            float localT = (t - 0.15) / 0.40;
            fireCol = mix(COL_DARK_GOLD, COL_DARK_GREEN, localT);
        } else {
            float localT = (t - 0.55) / 0.45;
            fireCol = mix(COL_DARK_GREEN, COL_CHARRED, localT);
        }

        float blendToReal = smoothstep(0.65, 1.0, t);
        color.rgb = mix(fireCol, color.rgb, blendToReal);
        color.a = 1.0;
    }

    vec4 lightMap = texture(Sampler2, texCoord2);
    fragColor = color * lightMap;
}