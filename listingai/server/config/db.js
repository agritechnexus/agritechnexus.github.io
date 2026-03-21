const mongoose = require('mongoose');

const connectDB = async () => {
  const maxRetries = 5;
  for (let i = 0; i < maxRetries; i++) {
    try {
      const conn = await mongoose.connect(process.env.MONGODB_URI || 'mongodb://localhost:27017/listingai');
      console.log(`MongoDB connected: ${conn.connection.host}`);
      return;
    } catch (err) {
      console.error(`MongoDB connection attempt ${i + 1} failed:`, err.message);
      if (i < maxRetries - 1) {
        await new Promise(r => setTimeout(r, 2000 * (i + 1)));
      } else {
        console.error('MongoDB connection failed after all retries');
        process.exit(1);
      }
    }
  }
};

module.exports = connectDB;
