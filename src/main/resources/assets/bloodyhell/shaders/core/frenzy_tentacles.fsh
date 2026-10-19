#version 150

uniform float Time;
uniform float Intensity;
uniform vec2 Resolution;
uniform float Seed;

in vec2 texCoord0;
out vec4 fragColor;

void main() {
    if (Intensity <= 0.0) {
        fragColor = vec4(0.0);
        return;
    }

    float pixelSize = 6.0;
    vec2 gridRes = Resolution / pixelSize;
    vec2 pUV = floor(texCoord0 * gridRes) / gridRes;

    vec2 uv = pUV;
    uv.y -= 0.5;

    float maxLength = 0.8 * Intensity;
    float uniqueThickness = 0.10 + mod(Seed, 0.08);
    float uniqueFlexibility = 0.4 + mod(Seed, 0.4);

    float bendAmp = uv.x * uniqueFlexibility;

    float dynamicSpeed = Time * mix(1.2, 3.5, Intensity);

    float speed1 = dynamicSpeed * (0.8 + mod(Seed, 1.2));
    float speed2 = dynamicSpeed * (1.2 + mod(Seed, 0.7));


    float wave = sin(uv.x * 8.0 - speed1 + Seed * 13.0) * bendAmp;
    wave += cos(uv.x * 14.0 - speed2) * (bendAmp * 0.4);


    wave += sin(uv.x * 25.0 - speed1 * 1.5) * (bendAmp * 0.1);

    float distortedY = uv.y - wave;


    float taper = clamp(1.0 - (uv.x / maxLength), 0.0, 1.0);
    taper = pow(taper, 1.3);

    float currentThickness = uniqueThickness * taper;

    float alpha = 0.0;
    if (uv.x > 0.0 && uv.x < maxLength) {
        alpha = step(abs(distortedY), currentThickness);
    }

    alpha *= step(0.01, Intensity);

    vec3 color = vec3(0.05, 0.0, 0.0);

    fragColor = vec4(color, alpha);
}