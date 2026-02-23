#version 150

#define PI 3.14159265


uniform float EtherealTime;


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
    float amp = 1.0; float freq = 1.0; float n = 0.0; float totalAmp = 0.0;
    for (int i = 0; i < 4; i++){
        n += amp * noise(p, freq); totalAmp += amp; amp *= 0.5; freq *= 2.0;
        if (i >= octaves) break;
    }
    return n / totalAmp;
}
//Ref https://www.shadertoy.com/view/lcfyDj
void main() {

    float time = mod(EtherealTime, 1000.0) / 20.0;
    vec2 centerUV = texCoord0 * 2.0 - 1.0;


    float radius = length(centerUV);


    float angle = atan(centerUV.y, centerUV.x);



    float spinSpeed = time * 2.0;


    float swirlAngle = angle + (1.5 - radius) * 4.0 - spinSpeed;

    vec2 swirlUV = vec2(cos(swirlAngle), sin(swirlAngle)) * radius;

    vec2 noiseUV1 = swirlUV * 10.0;

    vec2 noiseUV2 = (swirlUV * 8.0) - vec2(time * 2.0);


    float layer1 = pNoise(noiseUV1, 2);
    float layer2 = pNoise(noiseUV2, 2);


    float fire = pow(layer1 * layer2, 2.0) * 15.0;
    float innerFade = smoothstep(0.1, 0.2, radius);


    float outerFade = 1.0 - smoothstep(0.6, 1.0, radius);


    float ringMask = innerFade * outerFade;


    float finalIntensity = fire * ringMask;
    float darkenRadius = smoothstep(0.4, 1.0, radius) ;
    float brightnessRadius = 1.0 - smoothstep(0.0, 0.4, radius);
    float darknessBoost = 0.8;

    vec3 finalColor = (vertexColor.rgb - (darkenRadius * darknessBoost) + brightnessRadius ) * finalIntensity * 3.0;

    float finalAlpha = clamp(finalIntensity * 2.0, 0.0, 1.0) * vertexColor.a;

    fragColor = vec4(finalColor, finalAlpha);
}