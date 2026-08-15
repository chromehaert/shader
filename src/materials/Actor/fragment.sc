$input v_color0, v_fog, v_light, v_texcoord0

#include <bgfx_shader.sh>

#ifndef DEPTH_ONLY_OPAQUE
  SAMPLER2D_AUTOREG(s_MatTexture);
  #ifdef MASKED_MULTITEXTURE
    SAMPLER2D_AUTOREG(s_MatTexture1);
  #endif

  uniform vec4 MatColor;
  uniform vec4 ColorBased;
  uniform vec4 OverlayColor;
  #ifdef CHANGE_COLOR
    uniform vec4 ChangeColor;
  #endif
  #ifdef CHANGE_COLOR_MULTI
    uniform vec4 MultiplicativeTintColor;
  #endif
#endif

void main() {
  #ifndef DEPTH_ONLY_OPAQUE
    vec4 texColor = MatColor * texture2D(s_MatTexture, v_texcoord0);

    #ifdef MASKED_MULTITEXTURE
      // second texture layer (eg. a colored overlay) replaces the base layer
      // anywhere it has actual color data
      vec4 maskColor = texture2D(s_MatTexture1, v_texcoord0);
      bool useMaskLayer = (maskColor.r + maskColor.g + maskColor.b) * (1.0 - maskColor.a) > 0.0;
      texColor = useMaskLayer ? maskColor : texColor;
    #endif

    #ifdef ALPHA_TEST
      if (texColor.a < 0.5) {
        discard;
      }
    #endif

    #ifdef CHANGE_COLOR_MULTI
      vec3 tinted = mix(texColor.rrr * ChangeColor.rgb, texColor.ggg * MultiplicativeTintColor.rgb, vec3(ceil(texColor.g)));
      texColor.rgb = tinted;
    #elif defined(CHANGE_COLOR)
      vec3 tinted = mix(texColor.rgb, texColor.rgb * ChangeColor.rgb, vec3(texColor.a));
      texColor.rgb = tinted;
      texColor.a *= ChangeColor.a;
    #endif

    texColor.a = max(texColor.a, 0.0);

    // ColorBased.x blends in the mesh's vertex color (used for things like leather armor dye)
    vec3 litColor = mix(texColor.rgb * mix(vec3(1.0), v_color0.rgb, ColorBased.x), OverlayColor.rgb, OverlayColor.a);

    #ifdef EMISSIVE
      // glowing parts (low alpha) skip lightmap shading and stay full-bright
      litColor *= mix(vec3(1.0), v_light.rgb, vec3(texColor.a));
    #else
      litColor *= v_light.rgb;
    #endif

    vec4 color = vec4(litColor, texColor.a);
    color.rgb = mix(color.rgb, v_fog.rgb, v_fog.a);
    color.a = 1.0;

    gl_FragColor = color;
  #else
    gl_FragColor = vec4_splat(1.0);
  #endif
}
