#version 150

in vec4 vertexColor;
in vec3 vViewPos;

out vec4 fragColor;

void main() {

    vec3 dpdx = dFdx(vViewPos);
    vec3 dpdy = dFdy(vViewPos);
    vec3 normal = normalize(cross(dpdx, dpdy));

    vec3 viewDir = normalize(-vViewPos);


    float nDotV = abs(dot(viewDir, normal));

    float rimFactor = 1.0 - nDotV;
    rimFactor = smoothstep(0.5, 1.0, rimFactor);

    float alpha = pow(rimFactor, 3.0) * vertexColor.a;

    if (alpha <= 0.01) {
        discard;
    }

    fragColor = vec4(vertexColor.rgb, alpha);
}