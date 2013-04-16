#version 120

varying vec4 vColor;

void main() {
	gl_FrontColor = gl_Color;
	vColor = gl_FrontColor;
	gl_TexCoord[0] = gl_MultiTexCoord0;
	gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
}