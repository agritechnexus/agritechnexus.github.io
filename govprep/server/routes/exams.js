const express = require('express');
const ExamSyllabus = require('../models/ExamSyllabus');
const router = express.Router();

// GET /api/exams — list all supported exams
router.get('/', async (req, res) => {
  try {
    const exams = await ExamSyllabus.find({}, 'examName');
    res.json(exams.map(e => e.examName));
  } catch (err) {
    res.status(500).json({ msg: 'Server error' });
  }
});

// GET /api/exams/:examName/syllabus
router.get('/:examName/syllabus', async (req, res) => {
  try {
    const exam = await ExamSyllabus.findOne({ examName: req.params.examName });
    if (!exam) return res.status(404).json({ msg: 'Exam not found' });
    res.json(exam);
  } catch (err) {
    res.status(500).json({ msg: 'Server error' });
  }
});

module.exports = router;
