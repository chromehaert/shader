#ifndef INSTANCING
  $input v_color0
#endif

#include <bgfx_shader.sh>

void main() {
  #ifndef INSTANCING
    gl_FragColor = v_color0;
  #else
    gl_FragColor = vec4(0.0, 0.0, 0.0, 0.0);
  #endif
}
