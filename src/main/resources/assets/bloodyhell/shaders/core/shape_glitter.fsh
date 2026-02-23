#version 150

uniform float GlitterTime;

#define PI 3.14159265


in vec4 vertexColor;
in vec3 localPos;

out vec4 fragColor;

float rand(vec2 c){
    return fract(sin(dot(c.xy ,vec2(12.9898,78.233))) * 43758.5453);
}

float noise(vec2 p, float freq ){
    vec2 ij = floor(p * freq);
    vec2 xy = fract(p * freq);
    xy = .5*(1.-cos(PI*xy));
    float a = rand((ij+vec2(0.,0.)));
    float b = rand((ij+vec2(1.,0.)));
    float c = rand((ij+vec2(0.,1.)));
    float d = rand((ij+vec2(1.,1.)));
    return mix(mix(a, b, xy.x), mix(c, d, xy.x), xy.y);
}

void main() {
    vec4 baseColor = vertexColor;


    vec2 uv = localPos.xy * 100.0;

    float realSeconds = mod(GlitterTime, 1000.0) / 20.0;

    vec2 posR = (uv * 1.1) + vec2(realSeconds * -0.5);
    float noiseR = noise(posR, 1.0);

    vec2 posG = (uv * 0.9) + vec2(realSeconds * 0.5);
    float noiseG = noise(posG, 1.0);

    float result = noiseR * noiseG;
    result = pow(result, 12.0);

    vec3 glitterColor = vec3(1.0, 0.9, 0.3) * 5.0;

    vec3 finalColor = baseColor.rgb + (glitterColor * result);


    float finalAlpha = clamp(baseColor.a + (result * 3.0), 0.0, 1.0);

    fragColor = vec4(finalColor, finalAlpha);
}