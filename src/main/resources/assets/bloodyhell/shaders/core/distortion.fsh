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

    // --- NEW QUANTIZED ROTATION LOGIC ---
    // 1. Define how many "flat rings" you want
    float numRings = 1000.0;

    // 2. Quantize the distance into steps (0, 1, 2, 3...)
    float steppedDist = floor(dist * numRings) / numRings;

    // 3. Apply rotation based on the STEPPED distance
    // This ensures every pixel in the same ring gets the same rotation value
    float twistStrength = 8.0;
    // We add GameTime to make the rings actually revolve over time
    float revolveEffect = (twistStrength / (steppedDist + 0.05)) + (GameTime * 50.0);

    float newAngle = angle + revolveEffect;

    // 4. Calculate Offset
    // Using the original 'dist' for deformation keeps the shape circular,
    // but using 'newAngle' (which is stepped) creates the ring rotation.
    float lensStrength = 0.2;
    float deformation = dist * lensStrength;

    vec2 offset;
    offset.x = cos(newAngle) * deformation;
    offset.y = sin(newAngle) * deformation;

    // 5. SAMPLE & MASKING
    vec4 worldColor = texture(Sampler0, screenUV - offset);

    // Adjusted thresholds to match your core size requirements
    if (dist < 0.25) {
        fragColor = vec4(0.0, 0.0, 0.0, 1.0); // The Void
    } else if (dist > 0.5) {
        discard; // Keep it a circle
    } else {
        float laneScale = 60.0;
        float lane = floor(dist * laneScale);

        // Get a random value for this specific lane
        float randomValue = hash(lane);

        // 2. Control Density and Thickness
        // Only lanes with a random value > 0.92 will show a stripe (makes them rare)
        float stripeThreshold = 0.92;
        float isSlice = step(stripeThreshold, randomValue);

        // Optional: Make them pulse or move by adding GameTime to the hash input
        // float isSlice = step(stripeThreshold, hash(lane + floor(GameTime * 10.0)));

        // 3. Mixing
        vec3 worldColorDistorted = worldColor.rgb * (1.0 + (0.02 / dist));

        // We multiply isSlice by 0.5 to keep it subtle/translucent
        vec3 finalColorWithSlices = mix(worldColorDistorted, vec3(1.0, 1.0, 1.0), isSlice * 0.5);

        fragColor = vec4(finalColorWithSlices, 1.0) * vertexColor;
    }
}