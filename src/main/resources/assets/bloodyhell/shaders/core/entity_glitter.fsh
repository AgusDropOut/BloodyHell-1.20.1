#version 150


uniform sampler2D Sampler0;
uniform float GlitterTime;

#define PI 3.14159265


in vec2 texCoord0;
in vec4 vertexColor;

out vec4 fragColor;

//Perlin noise from https://gist-github-com.translate.goog/patriciogonzalezvivo/670c22f3966e662d2f83?_x_tr_sl=en&_x_tr_tl=es&_x_tr_hl=es&_x_tr_pto=tc
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


float pNoise(vec2 p, int octaves){
    float amp = 1.0;
    float freq = 1.0;
    float n = 0.0;
    float totalAmp = 0.0;

    for (int i = 0; i < 4; i++){
        n += amp * noise(p, freq);
        totalAmp += amp;
        amp *= 0.5;
        freq *= 2.0;
        if (i >= octaves) break;
    }
    return n / totalAmp;
}


//Tune from https://www.shadertoy.com/view/XtKyzD
void main() {

    vec4 baseColor = texture(Sampler0, texCoord0) * vertexColor;


    vec2 uv = texCoord0 * 1000.0;

    float realSeconds = mod(GlitterTime, 1000.0) / 20.0;



    vec2 posR = (uv * 1.1) + vec2(realSeconds * -0.5);
    float noiseR = noise(posR, 1.0);


    vec2 posG = (uv * 0.9) + vec2(realSeconds * 0.5);
    float noiseG = noise(posG, 1.0);


    float result = noiseR * noiseG;
    result = pow(result, 12.0);

    vec3 glitterColor = vec3(1.0, 0.9, 0.3) * 50.0;

    float dynamicAlpha = clamp(result * 3.0, 0.0, 1.0);

    vec3 finalColor =  (glitterColor * result);

    fragColor = vec4(finalColor, dynamicAlpha);
}