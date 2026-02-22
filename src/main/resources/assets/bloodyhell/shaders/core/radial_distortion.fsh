#version 150

uniform sampler2D Sampler0;
uniform vec2 ScreenSize;
uniform float GameTime;

in vec2 texCoord;
in vec4 vertexColor;

out vec4 fragColor;

void main() {
    vec2 screenUV = gl_FragCoord.xy / ScreenSize;
    vec2 localUV = texCoord - 0.5;
    float dist = length(localUV);


    if (dist > 0.5) {
        discard;
    }


    float innerMask = smoothstep(0.05, 0.15, dist);

    float outerMask = 1.0 - smoothstep(0.35, 0.5, dist);


    float distortionMask = innerMask * outerMask;


    vec2 pushDirection = vec2(0.0);
    if (dist > 0.001) {
        pushDirection = normalize(localUV);
    }


    float waveFrequency = 20.0;
    float waveSpeed = 10.0;
    float ripple = sin(dist * waveFrequency - GameTime * waveSpeed);

    float basePushStrength = 0.02;

    float edgeFade = 1.0 - smoothstep(0.4, 0.5, dist);

    float pushStrength = basePushStrength * edgeFade;

    vec2 offset = pushDirection * ripple * pushStrength * distortionMask;
    vec4 worldColor = texture(Sampler0, screenUV - offset);


    vec3 finalColor = worldColor.rgb;

    fragColor = vec4(finalColor, 1.0) * vertexColor;
}