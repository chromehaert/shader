#ifndef INSTANCING
  $input a_color0, a_position
  $output v_color0
#endif

#include <bgfx_shader.sh>

#ifndef INSTANCING
  uniform vec4 SkyColor;
  uniform vec4 FogColor;
  uniform vec4 FogAndDistanceControl;
  uniform vec4 ViewPositionAndTime;
#endif

void main() {
  #ifndef INSTANCING
    vec4 pos = vec4(a_position.xzy, 1.0);
    pos.xy = 2.0*clamp(pos.xy, -0.5, 0.5);

    vec4 v_color0 = mix(SkyColor, FogColor, a_color0.x);

    gl_Position = pos;
  #else
    gl_Position = vec4(0.0, 0.0, 0.0, 0.0);
  #endif
}
