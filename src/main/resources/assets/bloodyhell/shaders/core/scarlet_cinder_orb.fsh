#version 150

in vec3 v_ViewPos;
in vec2 v_ScreenUV;

uniform sampler2D Sampler0;
uniform sampler2D Sampler1;
uniform mat4 u_InvProjMat;
uniform vec3 u_OrbViewPos;
uniform mat3 u_OrbRotation;
uniform float u_Time;
uniform float u_Charge;
uniform float u_ChargeTime;
uniform float u_Flash;
uniform float u_Pitch;

out vec4 fragColor;


#define GLOBAL_SCALE (0.15 * (1.0 + 0.18 * u_Charge))

#define POS_Y 1.0

#define BASE_RADIUS 0.6
#define STRETCH_Y 0.4
#define TAPER_START -0.2
#define TAPER_END 0.82

#define FIRE_SPEED 3.0
#define NOISE_SCALE 2.7
#define NOISE_STRENGTH 0.3


#define SPHERE_BASE_COUNT 8
#define SPHERE_MAX_COUNT 16
#define SPHERE_SPEED 0.8
#define SPHERE_MAX_RADIUS 0.28
#define SPHERE_SIZE_VAR 0.75
#define SPHERE_SPREAD 0.45
#define SPHERE_BLEND 0.18
#define SPHERE_SPAWN_Y_MIN -0.6
#define SPHERE_SPAWN_Y_MAX 1.5
#define SPHERE_TRAVEL_DIST 3.5

#define RAY_STEP 0.4
#define MAX_STEPS 250
#define MAX_DIST 155.0

#define COLOR_BG vec3(0.3, 0.3, 0.3)
#define COLOR_BASE_WHITE vec3(1.0, 0.4, 1.0)
#define COLOR_CORE_BLACK vec3(0.0, 0.0, 0.0)
#define COLOR_FIRE_RED vec3(0.9, 0.0, 0.05)
#define COLOR_OUTLINE_BLACK vec3(0.0, 0.0, 0.0)
#define COLOR_GLOW_RED vec3(0.0, 0.0, 0.0)

#define FRESNEL_RED_START -0.2
#define FRESNEL_RED_END 0.5

#define GRAD_Y_MIN -0.2
#define GRAD_Y_MID 0.2

#define RADIAL_GLOW_POS_Y -2.0
#define RADIAL_GLOW_STRENGTH 5.5
#define RADIAL_GLOW_RADIUS 1.3
#define RADIAL_GLOW_FALLOFF 1.5

#define OUTLINE_MIN 0.30
#define OUTLINE_MAX 0.45

#define GLOW_MIN 0.25
#define GLOW_MAX 0.30
#define GLOW_INTENSITY 2.5



float hash(float n) { return fract(sin(n) * 43758.5453); }

float smin(float a, float b, float k) {
    float h = clamp(0.5 + 0.5 * (b - a) / k, 0.0, 1.0);
    return mix(b, a, h) - k * h * (1.0 - h);
}

float noise(vec3 x) {
    vec3 p = floor(x);
    vec3 f = fract(x);
    f = f * f * (3.0 - 2.0 * f);
    float n = p.x + p.y * 57.0 + 113.0 * p.z;
    return mix(
    mix(mix(hash(n +   0.0), hash(n +   1.0), f.x),
    mix(hash(n +  57.0), hash(n +  58.0), f.x), f.y),
    mix(mix(hash(n + 113.0), hash(n + 114.0), f.x),
    mix(hash(n + 170.0), hash(n + 171.0), f.x), f.y), f.z);
}

float sdDrop(vec3 p) {
    p.y += 0.5;
    vec3 q = p;
    q.y *= STRETCH_Y;

    float taper = 1.0 - smoothstep(TAPER_START, TAPER_END, q.y);
    q.xz /= (taper + 0.3);

    float d = length(q) - BASE_RADIUS;
    return d * min(1.0, taper + 0.6) * 1.5;
}

float map(vec3 p, float time, vec3 worldUpLocal) {
    p /= GLOBAL_SCALE;

    p.y -= POS_Y;
    float baseDist = sdDrop(p);

    float spheresDist = 1e5;
    for(int i = 0; i < SPHERE_MAX_COUNT; i++) {
        float sphereWeight = 1.0;
        if (i >= SPHERE_BASE_COUNT) {
            sphereWeight = clamp(u_Charge * float(SPHERE_MAX_COUNT - SPHERE_BASE_COUNT) - float(i - SPHERE_BASE_COUNT), 0.0, 1.0);
            if (sphereWeight <= 0.0) break;
        }
        float fi = float(i);
        float t = time * SPHERE_SPEED + fi * 1.618;

        float cycle = floor(t);
        float progress = fract(t);

        float h1 = hash(fi * 13.37 + cycle);
        float h2 = hash(fi * 42.0 + cycle);
        float h3 = hash(fi * 7.11 + cycle);

        float spawnY = mix(SPHERE_SPAWN_Y_MIN, SPHERE_SPAWN_Y_MAX, h1);
        vec3 startPos = vec3(0.0, spawnY, 0.0);


        vec3 upDir = vec3(0.0, 1.0, 0.0);
        if (mod(fi, 2.0) > 0.5) {
            upDir = worldUpLocal;
        }


        vec3 right = normalize(cross(upDir, vec3(0.0, 0.0, 1.0)));
        if (length(right) < 0.1) right = normalize(cross(upDir, vec3(1.0, 0.0, 0.0)));
        vec3 fwd = cross(right, upDir);


        float currentTravel = progress * SPHERE_TRAVEL_DIST * mix(0.7, 1.3, h2);

        float angle = time * 2.5 + fi * 2.399;
        float spread = SPHERE_SPREAD * (0.2 + 0.8 * progress) * mix(0.5, 1.5, h3);
        vec3 spiralOffset = (right * cos(angle) + fwd * sin(angle)) * spread;


        vec3 spherePos = startPos + (upDir * currentTravel) + spiralOffset;


        float curve = pow(progress, mix(0.6, 1.4, h1));
        float sizeLifecycle = sin(curve * 3.1415);

        float r = SPHERE_MAX_RADIUS * mix(1.0 - SPHERE_SIZE_VAR, 1.0, h2) * sizeLifecycle * sphereWeight;

        float dSphere = length(p - spherePos) - r;

        spheresDist = smin(spheresDist, dSphere, SPHERE_BLEND);
    }


    baseDist = smin(baseDist, spheresDist, SPHERE_BLEND);

    vec3 texCoord = p;
    texCoord.y -= time * FIRE_SPEED;
    texCoord *= NOISE_SCALE;

    float displacement = noise(texCoord);

    return (baseDist - (displacement * NOISE_STRENGTH)) * GLOBAL_SCALE;
}

vec3 getNormal(vec3 p, float time, vec3 worldUpLocal) {
    vec2 e = vec2(0.01, 0.0);
    return normalize(vec3(
    map(p + e.xyy, time, worldUpLocal) - map(p - e.xyy, time, worldUpLocal),
    map(p + e.yxy, time, worldUpLocal) - map(p - e.yxy, time, worldUpLocal),
    map(p + e.yyx, time, worldUpLocal) - map(p - e.yyx, time, worldUpLocal)
    ));
}

void main() {
    vec3 ro = vec3(0.0);
    vec3 rd = normalize(v_ViewPos);

    float distToQuad = length(v_ViewPos);
    float t = max(0.0, distToQuad - 1.5);

    float sceneDepth = texture(Sampler1, v_ScreenUV).r;
    float maxT = 99999.0;

    if (sceneDepth < 0.9999) {
        vec2 ndc = v_ScreenUV * 2.0 - 1.0;
        vec4 sceneClip = vec4(ndc, sceneDepth * 2.0 - 1.0, 1.0);
        vec4 sceneView = u_InvProjMat * sceneClip;
        sceneView /= sceneView.w;
        maxT = length(sceneView.xyz);
    }

    mat3 invOrbRot = transpose(u_OrbRotation);


    float pitchRad = radians(u_Pitch);
    vec3 worldUpView = vec3(0.0, cos(pitchRad), sin(pitchRad));
    vec3 worldUpLocal = normalize(invOrbRot * worldUpView);

    bool hit = false;
    vec3 p;

    for(int i = 0; i < MAX_STEPS; i++) {
        p = ro + rd * t;

        if (t > maxT - 0.05) break;

        vec3 localP = p - u_OrbViewPos;
        vec3 alignedP = invOrbRot * localP;

        float d = map(alignedP, u_ChargeTime, worldUpLocal);

        if(d < 0.005) {
            hit = true;
            break;
        }

        if (t > distToQuad + 1.5) break;

        t += d * RAY_STEP;
    }

    if(hit) {
        vec3 localP = p - u_OrbViewPos;
        vec3 alignedP = invOrbRot * localP;

        vec3 scaledP = alignedP / GLOBAL_SCALE;

        vec3 nLocal = getNormal(alignedP, u_ChargeTime, worldUpLocal);
        vec3 localViewDir = normalize(invOrbRot * (-rd));

        float localY = scaledP.y - POS_Y;
        float percentY = clamp((localY + 1.7) / 2.8, 0.0, 1.0);

        float NdotV = max(dot(nLocal, localViewDir), 0.0);
        float fresnel = 1.0 - NdotV;

        float redMask = smoothstep(FRESNEL_RED_START, FRESNEL_RED_END, fresnel);
        vec3 bodyColor = mix(COLOR_CORE_BLACK, COLOR_FIRE_RED, redMask);

        float whiteBaseMask = 1.0 - smoothstep(GRAD_Y_MIN, GRAD_Y_MID, percentY);
        bodyColor = mix(bodyColor, COLOR_BASE_WHITE, whiteBaseMask);

        float dCenter = length(scaledP - vec3(0.0, POS_Y + RADIAL_GLOW_POS_Y, 0.0));
        float radialGlow = clamp(1.0 - (dCenter / RADIAL_GLOW_RADIUS), 0.0, 1.0);
        radialGlow = pow(radialGlow, RADIAL_GLOW_FALLOFF) * RADIAL_GLOW_STRENGTH;

        bodyColor += vec3(1.0, 1.0, 1.0) * radialGlow;
        bodyColor += vec3(1.0, 0.3, 0.3) * u_Flash * 0.7;

        float outlineMask = 1.0 - smoothstep(OUTLINE_MIN, OUTLINE_MAX, NdotV);
        vec3 col = mix(bodyColor, COLOR_OUTLINE_BLACK, outlineMask);

        float glowMask = 1.0 - smoothstep(GLOW_MIN, GLOW_MAX, NdotV);
        col += COLOR_GLOW_RED * glowMask * GLOW_INTENSITY;

        fragColor = vec4(col, 1.0);
    } else {
        discard;
    }
}