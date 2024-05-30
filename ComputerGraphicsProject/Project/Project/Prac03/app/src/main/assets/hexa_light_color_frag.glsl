#version 300 es
precision mediump float;

uniform vec4 fColor;
uniform vec3 lightAmbi, lightDir, lightDiff, lightSpec;
uniform vec3 matAmbi, matSpec;
uniform float matSh;

in vec3 fNormal, fView;

out vec4 fragColor;

void main() {
    vec3 normal = normalize(fNormal);
    vec3 light = normalize(lightDir);
    vec3 view = normalize(fView);

    vec3 ambi = lightAmbi * matAmbi;

    vec3 diff = vec3(0.0, 0.0, 0.0);
    vec3 spec = vec3(0.0, 0.0, 0.0);

    vec3 matDiff = fColor.rgb;
    float dotNL = dot(normal, light);
    if(dotNL > 0.0) {
        diff = dotNL * lightDiff * matDiff;

        vec3 refl = 2.0 * normal * dotNL - light;
        spec = pow(max(dot(refl, view),0.0), matSh) * lightSpec * matSpec;
    }

    //fragColor = texture(sTexture, fTexCoord);
    fragColor = vec4(ambi + diff + spec, 1.0);
}