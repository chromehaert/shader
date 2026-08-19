#ifndef INSTANCING
  $input v_color0
#endif

#include <bgfx_shader.sh>

#ifndef INSTANCING
  uniform vec4 TimeOfDay;
  uniform vec4 Day;
  uniform vec4 FogColor;
  uniform vec4 FogAndDistanceControl;
#endif

void main() {
  #ifndef INSTANCING
    gl_FragColor = v_color0;
  #else
    gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
  #endif
}
