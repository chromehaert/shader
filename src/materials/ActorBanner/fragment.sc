$input v_color0, v_fog, v_light, v_texcoord0, v_texcooords

#include <bgfx_shader.sh>
#include <MinecraftRenderer.Materials/ActorUtil.dragonh>

uniform vec4 ColorBased;
uniform vec4 ChangeColor;
uniform vec4 UseAlphaRewrite;
uniform vec4 TintedAlphaTestEnabled;
uniform vec4 MatColor;
uniform vec4 OverlayColor;
uniform vec4 MultiplicativeTintColor;
uniform vec4 ActorFPEpsilon;
uniform vec4 HudOpacity;

SAMPLER2D_AUTOREG(s_MatTexture);
SAMPLER2D_AUTOREG(s_MatTexture1);

void main() {
  #if defined(DEPTH_ONLY) || defined(INSTANCING)
    gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
    return;
  #elif defined(DEPTH_ONLY_OPAQUE)
    gl_FragColor = vec4(mix(vec3_splat(1.0), v_fog.rgb, v_fog.a), 1.0);
    return;
  #endif

#if !ALPHA_TEST
    vec4 diffuse = texture2D(s_MatTexture, v_texcoords.xy);
    vec4 base = texture2D(s_MatTexture, v_texcoords.zw);

    #if TINTING
      base.a = mix(diffuse.r * diffuse.a, diffuse.a, v_color0.a);
      base.rgb *= v_color0.rgb;
    #endif

    base = applyLighting(base, v_light);
    base = applyHudOpacity(base, HudOpacity.x);
    base.rgb = applyFog(base.rgb, v_fog.rgb, v_fog.a);

  gl_FragColor = base;
#else
  vec4 albedo = getActorAlbedoNoColorChange(v_texcoord0, s_MatTexture, s_MatTexture1, MatColor);

  #ifdef ALPHA_TEST
    float alpha = mix(albedo.a, (albedo.a * OverlayColor.a), TintedAlphaTestEnabled.x);
    if (shouldDiscard(albedo.rgb, alpha, ActorFPEpsilon.x)) {
      discard;
    }
  #endif

  #ifdef CHANGE_COLOR_MULTI
    albedo = applyMultiColorChange(albedo, ChangeColor.rgb, MultiplicativeTintColor.rgb);
  #elif defined(CHANGE_COLOR)
    albedo = applyColorChange(albedo, ChangeColor, albedo.a);
    albedo.a *= ChangeColor.a;
  #endif

  #ifdef ALPHA_TEST
    albedo.a = max(UseAlphaRewrite.r, albedo.a);
  #endif

  albedo.rgb *= mix(vec3(1.0, 1.0, 1.0), v_color0.rgb, ColorBased.x);

  albedo = applyOverlayColor(albedo, OverlayColor);

  albedo = applyLighting(albedo, v_light);

  #ifdef TRANSPARENT
    albedo = applyHudOpacity(albedo, HudOpacity.x);
  #endif

  albedo.rgb = mix(albedo.rgb, v_fog.rgb, v_fog.a);

  gl_FragColor = albedo;
#endif // !ALPHA_TEST
}
