const mongoose = require('mongoose');
require('dotenv').config({ path: '../../.env' });
const ExamSyllabus = require('../models/ExamSyllabus');
const exams = require('./examData');

const MONGO_URI = process.env.MONGO_URI || 'mongodb://localhost:27017/govprep';

async function seed() {
  try {
    await mongoose.connect(MONGO_URI);
    console.log('MongoDB connected');

    await ExamSyllabus.deleteMany({});
    console.log('Cleared existing exam data');

    await ExamSyllabus.insertMany(exams);
    console.log(`Seeded ${exams.length} exams`);

    await mongoose.disconnect();
    console.log('Done');
  } catch (err) {
    console.error('Seed error:', err.message);
    process.exit(1);
  }
}

seed();
