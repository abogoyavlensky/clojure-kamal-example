/** @type {import('tailwindcss').Config} */

module.exports = {
  mode: 'jit',
  content: ['./src/cljs/front/**/*.cljs',
            './resources/public/index.html'],
  theme: {
    extend: {},
  },
  plugins: [
  ]
}
