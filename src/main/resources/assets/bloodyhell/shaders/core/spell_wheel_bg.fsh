#version 150

in vec2 texCoord0;
uniform sampler2D Sampler0;
uniform float TimeAnim;
uniform float TargetAngle;

out vec4 fragColor;

#define PI 3.14159265359

#define WEDGE_WIDTH 4.2


#define WEDGE_BLUR 0.15


float hash(vec2 p) { return fract(sin(dot(p, vec2(12.9898, 78.233))) * 43758.5453); }

float noise(vec2 p) {
    vec2 i = floor(p); vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);
    return mix(mix(hash(i + vec2(0.0,0.0)), hash(i + vec2(1.0,0.0)), u.x),
    mix(hash(i + vec2(0.0,1.0)), hash(i + vec2(1.0,1.0)), u.x), u.y);
}

void main() {
    vec2 uv = texCoord0 - 0.5;

    float radius = length(uv);
    float angle = atan(uv.y, uv.x);

    float angleDiff = abs(angle - TargetAngle);
    if (angleDiff > PI) angleDiff = 2.0 * PI - angleDiff;


    float wedgeAperture = PI / WEDGE_WIDTH;
    float wedgeMask = smoothstep(wedgeAperture, wedgeAperture * WEDGE_BLUR, angleDiff);

    float n = noise(uv * 22.0 - TimeAnim * 1.5);

    float radialMask = smoothstep(0.58, 0.08, radius + n * 0.04);
    float innerMask = smoothstep(0.08, 0.16, radius);

    float pulse = 0.75 + 0.15 * sin(TimeAnim * 5.0);

    float finalAlpha = wedgeMask * radialMask * innerMask * pulse;

    vec3 colOscuro = vec3(0.12, 0.0, 0.0);
    vec3 colRojo = vec3(0.75, 0.03, 0.03);
    vec3 colBrillo = vec3(0.95, 0.25, 0.08);

    vec3 color = mix(colOscuro, colRojo, finalAlpha * 1.5);
    if (finalAlpha > 0.55) {
        color = mix(color, colBrillo, (finalAlpha - 0.55) * 2.5);
    }

    fragColor = vec4(color * finalAlpha, finalAlpha);
}