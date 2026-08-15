$input a_color0, a_position, a_texcoord0
#ifdef FANCY
  $input a_normal
#endif
#if !defined(INSTANCING) || defined(FANCY)
  $input a_indices
#endif
#ifdef INSTANCING
  $input i_data1, i_data2, i_data3
#endif
$output v_color0, v_fog, v_light, v_texcoord0, v_worldPos

#include <bgfx_shader.sh>

#if !defined(INSTANCING) || defined(FANCY)
  uniform mat4 Bones[8];
#endif
uniform vec4 FogColor;
uniform vec4 FogControl;
uniform vec4 OverlayColor;
uniform vec4 SubPixelOffset;
uniform vec4 TileLightColor;
uniform vec4 UVAnimation;

void main() {
  #ifdef INSTANCING
    mat4 model = mtxFromRows(i_data1, i_data2, i_data3, vec4(0.0, 0.0, 0.0, 1.0));
  #else
    mat4 model = mul(u_model[0], Bones[int(a_indices)]);
  #endif

  vec3 worldPos = mul(model, vec4(a_position, 1.0)).xyz;

  #ifdef FANCY
    vec3 worldNormal = normalize(mul(model, vec4(a_normal.xyz, 0.0)).xyz);
    worldNormal.y *= TileLightColor.w;
  #endif

  // TAA sub-pixel jitter, applied directly to the projection matrix
  mat4 proj = u_proj;
  proj[2].x += SubPixelOffset.x;
  proj[2].y -= SubPixelOffset.y;

  vec4 clipPos = mul(proj, mul(u_view, vec4(worldPos, 1.0)));

  v_color0 = a_color0;
  v_fog = vec4(FogColor.rgb, clamp(((clipPos.z / FogControl.z) - FogControl.x) / (FogControl.y - FogControl.x), 0.0, 1.0));

  #ifdef FANCY
    // simple half-lambert style shading from the vertex normal, matching vanilla's per-vertex actor light
    v_light = vec4(TileLightColor.rgb * (0.45 + 0.275 * (1.0 + worldNormal.y) - 0.1 * worldNormal.x * worldNormal.x + 0.1 * worldNormal.z * worldNormal.z + OverlayColor.a * 0.35), 1.0);
  #else
    v_light = vec4(TileLightColor.rgb * (1.0 + OverlayColor.a * 0.35), 1.0);
  #endif

  v_texcoord0 = UVAnimation.xy + a_texcoord0 * UVAnimation.zw;
  v_worldPos  = worldPos;

  gl_Position = clipPos;
}
