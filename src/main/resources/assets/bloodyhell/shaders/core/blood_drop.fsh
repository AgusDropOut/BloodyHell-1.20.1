#version 150

uniform float AnimTime;
uniform vec4 ColorModulator;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

#define velocity 20.1
#define frecuency 30.1

float random(vec2 st) {
    return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
}

float hash(vec2 p) {
    p = 50.0 * fract(p * 0.3183099 + vec2(0.71, 0.113));
    return -1.0 + 2.0 * fract(p.x * p.y * (p.x + p.y));
}

float noise(vec2 p) {
    vec2 i = floor(p);
    vec2 f = fract(p);
    vec2 u = f * f * (3.0 - 2.0 * f);

    return mix(mix(hash(i + vec2(0.0, 0.0)), hash(i + vec2(1.0, 0.0)), u.x),
    mix(hash(i + vec2(0.0, 1.0)), hash(i + vec2(1.0, 1.0)), u.x), u.y);
}

void main() {
    vec2 uv = texCoord0;

    uv.y = -uv.y - 0.09;


    if (uv.y > 0.7 || uv.y < -0.5 || uv.x > 0.5 || uv.x < -0.5) {
        discard;
    }

    float wave = sin(uv.y * frecuency + AnimTime * velocity) * 0.005;
    uv.x += wave;

    float shapeBase = max(0.7 * uv.y + 0.9, 0.0);

    float d = (uv.x * uv.x * 0.8) + (uv.y * uv.y * pow(shapeBase, 3.0));


    if(d > 0.04) {
        discard;
    }

    float n1 = noise(uv * 1.0 + vec2(0.0, AnimTime * 1.9));
    float n2 = noise(uv * 7.0 - vec2(AnimTime * 0.4, 0.0)) * 1.5;
    float interiorPattern = clamp(n1 + n2, 0.0, 1.0);

    float edgeFactor = clamp(d / 0.04, 0.0, 1.0);

    vec3 deepBlood = vertexColor.rgb * 0.3;
    vec3 midBlood  = vertexColor.rgb * 0.8;
    vec3 brightRim = mix(vertexColor.rgb, vec3(1.0, 0.4, 0.4), 0.6);

    vec3 blood = mix(deepBlood, midBlood, interiorPattern);
    blood = mix(blood, brightRim, pow(edgeFactor, 2.0));

    vec2 lightOffset = uv - vec2(0.04 + sin(AnimTime * 0.2) * 0.03, -0.26);
    float highlight = exp(-dot(lightOffset, lightOffset) * 400.0);

    blood += vec3(1.0, 0.9, 0.9) * highlight * 0.5;

    fragColor = vec4(blood, vertexColor.a) * ColorModulator;
}