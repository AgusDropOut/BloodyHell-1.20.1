#version 150

in vec3 v_ViewPos;
in vec2 v_ScreenUV;

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform mat4 u_InvProjMat;
uniform vec3 u_OrbViewPos;
uniform mat3 u_OrbRotation;
uniform float u_Time;

out vec4 fragColor;


#define GEM_WIDTH 0.5
#define GEM_HEIGHT 0.8
#define GEM_DEPTH 0.5
#define GEM_SHARPNESS 0.973


#define ROTATION_SPEED 0.65


#define GROOVE_COUNT 4.0
#define SWIRL_TWIST 1.5
#define GROOVE_DEPTH 0.25
#define GROOVE_FADE_START 1.0
#define GROOVE_FADE_END 0.3


#define SWIRL_X_INFLUENCE 0.2
#define SWIRL_Y_INFLUENCE 0.6


#define GOLD_OPACITY 0.72
#define ABERRATION_STRENGTH 0.46
#define IOR_RED 1.05
#define IOR_GREEN 1.12
#define IOR_BLUE 1.20
#define GLASS_TINT vec3(1.0, 0.9, 0.7)


#define LIGHT_DIR vec3(1.5, 2.0, -1.8)
#define ORO_CLARO vec3(1.0, 0.82, 0.35)
#define ORO_OSCURO vec3(0.35, 0.16, 0.02)
#define SPECULAR_SHININESS 95.0
#define SPECULAR_INTENSITY 1.6
#define FRESNEL_POWER 0.8
#define FRESNEL_INTENSITY 0.65


#define MAX_MARCH_STEPS 95
#define HIT_THRESHOLD 0.001
#define RAY_STEP_FACTOR 0.2

#define GLOBAL_SCALE 0.35
#define SCALE (vec3(GEM_WIDTH, GEM_HEIGHT, GEM_DEPTH) * GLOBAL_SCALE)

mat2 rot(float a) {
    float s = sin(a), c = cos(a);
    return mat2(c, -s, s, c);
}

float sdBipyramid(vec3 p, vec3 s) {
    p = abs(p);
    return (p.x/s.x + p.y/s.y + p.z/s.z - 1.0) * min(s.x, min(s.y, s.z)) * GEM_SHARPNESS;
}

float map(vec3 p, float time) {
    p.xz *= rot(time * ROTATION_SPEED);

    float xFade = smoothstep(SCALE.x * GROOVE_FADE_START, SCALE.x * GROOVE_FADE_END, abs(p.x));
    float yFade = smoothstep(SCALE.y * GROOVE_FADE_START, SCALE.y * GROOVE_FADE_END, abs(p.y));

    float swirlDisp = 0.0;

    if (SWIRL_X_INFLUENCE > 0.0) {
        float angleX = atan(p.z, p.y);
        float swirlX = abs(sin(angleX * GROOVE_COUNT + p.x * SWIRL_TWIST * (1.0 / GLOBAL_SCALE)));
        swirlDisp += (swirlX - 0.5) * (GROOVE_DEPTH * GLOBAL_SCALE) * xFade * SWIRL_X_INFLUENCE;
    }

    if (SWIRL_Y_INFLUENCE > 0.0) {
        float angleY = atan(p.z, p.x);
        float swirlY = abs(sin(angleY * GROOVE_COUNT + p.y * SWIRL_TWIST * (1.0 / GLOBAL_SCALE)));
        swirlDisp += (swirlY - 0.5) * (GROOVE_DEPTH * GLOBAL_SCALE) * yFade * SWIRL_Y_INFLUENCE;
    }

    float gem = sdBipyramid(p, SCALE);
    gem += swirlDisp;

    return gem;
}

vec3 calcNormal(vec3 p, float time) {
    vec2 e = vec2(0.002, 0.0);
    return normalize(vec3(
    map(p + e.xyy, time) - map(p - e.xyy, time),
    map(p + e.yxy, time) - map(p - e.yxy, time),
    map(p + e.yyx, time) - map(p - e.yyx, time)
    ));
}

void main() {
    vec3 ro = vec3(0.0);
    vec3 rd = normalize(v_ViewPos);

    float distToQuad = length(v_ViewPos);
    float t = max(0.0, distToQuad - 1.0);

    float sceneDepth = texture(Sampler1, v_ScreenUV).r;
    vec2 ndc = v_ScreenUV * 2.0 - 1.0;
    float maxT = 99999.0;

    if (sceneDepth < 0.9999) {
        vec4 sceneClip = vec4(ndc, sceneDepth * 2.0 - 1.0, 1.0);
        vec4 sceneView = u_InvProjMat * sceneClip;
        sceneView /= sceneView.w;
        maxT = length(sceneView.xyz);
    }

    bool hit = false;
    vec3 p;
    mat3 invOrbRot = transpose(u_OrbRotation);

    for(int i = 0; i < MAX_MARCH_STEPS; i++) {
        p = ro + rd * t;

        if (t > maxT - 0.05) break;

        vec3 localP = p - u_OrbViewPos;
        vec3 alignedP = invOrbRot * localP;

        float d = map(alignedP + vec3(0.0,0.15,0.0), u_Time);

        if(d < HIT_THRESHOLD) {
            hit = true;
            break;
        }

        if (t > distToQuad + 1.0) break;

        t += d * RAY_STEP_FACTOR;
    }

    if(hit) {
        vec3 localP = p - u_OrbViewPos;
        vec3 alignedP = invOrbRot * localP;
        vec3 nLocal = calcNormal(alignedP, u_Time);

        vec3 nView = u_OrbRotation * nLocal;
        vec3 viewDir = rd;

        vec3 refR = refract(viewDir, nView, 1.0 / IOR_RED);
        vec3 refG = refract(viewDir, nView, 1.0 / IOR_GREEN);
        vec3 refB = refract(viewDir, nView, 1.0 / IOR_BLUE);

        vec2 uvR = clamp(v_ScreenUV + refR.xy * ABERRATION_STRENGTH, 0.001, 0.999);
        vec2 uvG = clamp(v_ScreenUV + refG.xy * ABERRATION_STRENGTH, 0.001, 0.999);
        vec2 uvB = clamp(v_ScreenUV + refB.xy * ABERRATION_STRENGTH, 0.001, 0.999);

        vec3 cristalRefractado = vec3(
        texture(Sampler0, uvR).r,
        texture(Sampler0, uvG).g,
        texture(Sampler0, uvB).b
        );

        vec3 lightDir = normalize(LIGHT_DIR);
        vec3 halfVec = normalize(lightDir - viewDir);

        float diff = max(dot(nView, lightDir), 0.0);
        float spec = pow(max(dot(nView, halfVec), 0.0), SPECULAR_SHININESS);
        float fresnel = pow(1.0 - max(dot(nView, -viewDir), 0.0), FRESNEL_POWER);

        vec3 baseOro = mix(ORO_OSCURO, ORO_CLARO, diff * 0.75 + 0.25);

        vec3 col = mix(cristalRefractado * GLASS_TINT, baseOro, GOLD_OPACITY);
        col += spec * vec3(1.0, 0.95, 0.8) * SPECULAR_INTENSITY;
        col += fresnel * ORO_CLARO * FRESNEL_INTENSITY;

        fragColor = vec4(col, 1.0);
    } else {
        discard;
    }
}