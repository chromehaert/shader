$input a_color0, a_position, a_texcoord0
#ifdef INSTANCING
  $input i_data1, i_data2, i_data3
#endif
$output v_color0, v_texcoord0, v_worldPos

#include <bgfx_shader.sh>

uniform vec4 CloudColor;
uniform vec4 FogColor;
uniform vec4 DistanceControl;
uniform vec4 SubPixelOffset;

void main() {
  #ifdef INSTANCING
    mat4 model;
    model[0] = vec4(i_data1.x, i_data2.x, i_data3.x, 0.0);
    model[1] = vec4(i_data1.y, i_data2.y, i_data3.y, 0.0);
    model[2] = vec4(i_data1.z, i_data2.z, i_data3.z, 0.0);
    model[3] = vec4(i_data1.w, i_data2.w, i_data3.w, 1.0);
  #else
    mat4 model = u_model[0];
  #endif

  vec3 worldPos = mul(model, vec4(a_position, 1.0)).xyz;

  // TAA sub-pixel jitter, applied directly to the projection matrix
  mat4 proj = u_proj;
  proj[2].x += SubPixelOffset.x;
  proj[2].y -= SubPixelOffset.y;

  vec4 color = a_color0 * CloudColor;

  // fade toward fog color with distance instead of fading to transparent
  float fogFade = clamp(1.0 - max((length(worldPos) / DistanceControl.x) - 0.9, 0.0), 0.0, 1.0);
  color.rgb = mix(FogColor.rgb, color.rgb, fogFade);

  v_color0    = color;
  v_texcoord0 = a_texcoord0;
  v_worldPos  = worldPos;

  gl_Position = mul(proj, mul(u_view, vec4(worldPos, 1.0)));
}
