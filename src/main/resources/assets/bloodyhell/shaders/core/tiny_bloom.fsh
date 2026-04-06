#version 150

in vec2 localPos;
in vec4 vertexColor;
out vec4 fragColor;

void main() {
    vec2 uv = localPos;

    float dist = length(uv);

    if (dist > 1.0) {
        discard;
    }

    float intensity = pow(max(0.0, 0.7 - dist), 1.5);

    float coreMask = smoothstep(0.3, 0.0, dist);
    vec3 colCore = vec3(1.0, 1.0, 0.95);
    vec3 colBloom = vertexColor.rgb;

    vec3 baseColor = mix(colBloom, colCore, coreMask);

    vec3 finalEmission = baseColor * intensity * 3.5;

    float alpha = smoothstep(0.0, 0.5, intensity) * 0.4;
    vec3 preMultipliedRGB = finalEmission * vertexColor.a;
    float preMultipliedAlpha = alpha * vertexColor.a;

    if (length(preMultipliedRGB) <= 0.01 && preMultipliedAlpha <= 0.01) {
        discard;
    }

    fragColor = vec4(preMultipliedRGB, preMultipliedAlpha);
}