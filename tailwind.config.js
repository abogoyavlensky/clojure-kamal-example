/** @type {import('tailwindcss').Config} */

module.exports = {
  mode: 'jit',
  content: ['./src/cljs/ui/**/*.cljs',
            './resources/public/index.html'],
  theme: {
    extend: {},
  },
  plugins: [
  ]
}
