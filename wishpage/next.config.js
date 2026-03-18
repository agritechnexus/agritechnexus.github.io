/** @type {import('next').NextConfig} */
const nextConfig = {
  output: 'export',
  basePath: '/wishpage',
  images: {
    unoptimized: true,
  },
  trailingSlash: true,
};
module.exports = nextConfig;
