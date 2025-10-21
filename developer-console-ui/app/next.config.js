/** @type {import('next').NextConfig} */
module.exports = {
  reactStrictMode: true,
  publicRuntimeConfig: {
    url: process.env.APP_DCO_GATEWAY_SERVICE_URL,
  },
  typescript: {
    // Temporarily ignore build errors to test mock data
    ignoreBuildErrors: true,
  },
  eslint: {
    // Temporarily ignore lint errors to test mock data
    ignoreDuringBuilds: true,
  },
}
