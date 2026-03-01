#version 150

uniform sampler2D Sampler0;
uniform vec4 ColorModulator;
uniform float GameTime;
uniform float InsightAlpha;

in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

void main() {

    float time = GameTime * 1000.0;


    vec2 glassWarp = vec2(
    abs(sin(texCoord0.y * 12.0 + time)) * cos(texCoord0.x * 18.0 - time * 0.8),
    abs(cos(texCoord0.x * 12.0 + time * 1.2)) * sin(texCoord0.y * 18.0 - time)
    );


    glassWarp = sign(glassWarp) * pow(abs(glassWarp), vec2(2.0)) * 0.2;


    float r = texture(Sampler0, texCoord0 + glassWarp * 1.3).r;
    float g = texture(Sampler0, texCoord0 + glassWarp * 1.0).g;
    float b = texture(Sampler0, texCoord0 + glassWarp * 0.7).b;


    float a = texture(Sampler0, texCoord0 + glassWarp).a;

    vec4 texColor = vec4(r, g, b, a);

    float edgeGlint = length(glassWarp);
    float highlight = smoothstep(0.005, 0.02, edgeGlint);


    vec3 finalColor = mix(texColor.rgb, vec3(1.0, 1.0, 1.0), highlight * 0.6);

    finalColor += vec3(0.02, 0.05, 0.08);


    vec4 color = vec4(finalColor, texColor.a) * vertexColor * ColorModulator;

    color.a *= InsightAlpha;

    if (color.a < 0.05) {
        discard;
    }

    fragColor = color;
}