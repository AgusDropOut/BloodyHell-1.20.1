#version 150

uniform sampler2D Sampler0;
uniform vec2 ScreenSize;
uniform float GameTime;

in vec2 texCoord;
in vec4 vertexColor;

out vec4 fragColor;

float hash(float n) {
    return fract(sin(n) * 43758.5453123);
}

void main() {
    vec2 screenUV = gl_FragCoord.xy / ScreenSize;
    vec2 localUV = texCoord - 0.5;
    float dist = length(localUV);
    float angle = atan(localUV.y, localUV.x);


    float numRings = 1000.0;

    float steppedDist = floor(dist * numRings) / numRings;


    float twistStrength = 8.0;

    float revolveEffect = (twistStrength / (steppedDist + 0.05)) + (GameTime * 50.0);

    float newAngle = angle + revolveEffect;


    float lensStrength = 0.2;
    float deformation = dist * lensStrength;

    vec2 offset;
    offset.x = cos(newAngle) * deformation;
    offset.y = sin(newAngle) * deformation;


    vec4 worldColor = texture(Sampler0, screenUV - offset);


    if (dist < 0.25) {
        fragColor = vec4(0.0, 0.0, 0.0, 1.0);
    } else if (dist > 0.5) {
        discard;
    } else {
        float laneScale = 60.0;
        float lane = floor(dist * laneScale);

        float randomValue = hash(lane);


        float stripeThreshold = 0.92;
        float isSlice = step(stripeThreshold, randomValue);


        vec3 worldColorDistorted = worldColor.rgb * (1.0 + (0.02 / dist));


        vec3 finalColorWithSlices = mix(worldColorDistorted, vec3(1.0, 1.0, 1.0), isSlice * 0.5);

        fragColor = vec4(finalColorWithSlices, 1.0) * vertexColor;
    }
}