$input v_color0, v_fog, v_light

#include <bgfx_shader.sh>
#include <MinecraftRenderer.Materials/ActorUtil.dragonh>

uniform vec4 ChangeColor;
uniform vec4 OverlayColor;
uniform vec4 ColorBased;
uniform vec4 MatColor;
uniform vec4 MultiplicativeTintColor;

void main() {
  #if defined(DEPTH_ONLY) || defined(INSTANCING)
    gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
    return;
  #endif

  vec4 albedo = vec4(mix(vec3(1.0, 1.0, 1.0), v_color0.rgb, ColorBased.x), 1.0);

  float albedoAlpha = albedo.a;

  #ifdef MULTI_COLOR_TINT
    albedo = applyMultiColorChange(albedo, ChangeColor.rgb, MultiplicativeTintColor.rgb);
  #else
    albedo = applyColorChange(albedo, ChangeColor, albedo.a);
    albedo.a *= ChangeColor.a;
  #endif

  albedo = applyOverlayColor(albedo, OverlayColor);

  #ifdef ALPHA_TEST
    if (albedo.a < 0.5) {
      discard;
    }
  #endif

  if (((albedoAlpha > 0.9875) && (albedoAlpha < 0.995))) {
    albedo.rgb *= vec3(1.0, 1.0, 1.0);
  } else {
    albedo = applyLighting(albedo, v_light);
  };

  albedo.rgb = mix(albedo.rgb, v_fog.rgb, v_fog.a);

  gl_FragColor = albedo;
}
