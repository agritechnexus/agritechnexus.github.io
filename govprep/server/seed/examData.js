const exams = [
  {
    examName: "SSC CGL",
    subjects: [
      {
        name: "Quantitative Aptitude",
        topics: ["Number System", "Algebra", "Geometry", "Trigonometry", "Mensuration", "Data Interpretation", "Percentage", "Ratio & Proportion", "Profit & Loss", "Time & Work", "Time & Distance", "SI & CI", "Average", "Mixture & Alligation"]
      },
      {
        name: "General Awareness",
        topics: ["Indian History", "Indian Polity", "Geography", "Economics", "General Science", "Current Affairs", "Static GK"]
      },
      {
        name: "English Language",
        topics: ["Reading Comprehension", "Cloze Test", "Error Spotting", "Sentence Improvement", "Synonyms & Antonyms", "Idioms & Phrases", "One Word Substitution", "Spelling Correction"]
      },
      {
        name: "Reasoning",
        topics: ["Analogy", "Classification", "Series", "Coding-Decoding", "Blood Relations", "Direction Sense", "Syllogism", "Venn Diagram", "Matrix", "Paper Folding", "Mirror Image", "Non-Verbal Reasoning"]
      }
    ]
  },
  {
    examName: "SSC CHSL",
    subjects: [
      {
        name: "Quantitative Aptitude",
        topics: ["Number System", "Algebra", "Geometry", "Trigonometry", "Percentage", "Ratio & Proportion", "Profit & Loss", "Time & Work", "Time & Distance", "SI & CI", "Average", "Data Interpretation"]
      },
      {
        name: "General Awareness",
        topics: ["Indian History", "Indian Polity", "Geography", "Economics", "General Science", "Current Affairs"]
      },
      {
        name: "English Language",
        topics: ["Reading Comprehension", "Error Spotting", "Sentence Improvement", "Synonyms & Antonyms", "Idioms & Phrases", "One Word Substitution", "Fill in the Blanks"]
      },
      {
        name: "Reasoning",
        topics: ["Analogy", "Classification", "Series", "Coding-Decoding", "Blood Relations", "Direction Sense", "Syllogism", "Venn Diagram", "Non-Verbal Reasoning"]
      }
    ]
  },
  {
    examName: "IBPS PO",
    subjects: [
      {
        name: "Quantitative Aptitude",
        topics: ["Number Series", "Data Interpretation", "Quadratic Equations", "Simplification", "Percentage", "Ratio & Proportion", "Profit & Loss", "Time & Work", "Time & Distance", "SI & CI", "Average", "Probability"]
      },
      {
        name: "Reasoning Ability",
        topics: ["Seating Arrangement", "Puzzles", "Syllogism", "Coding-Decoding", "Blood Relations", "Direction Sense", "Inequality", "Data Sufficiency", "Input-Output", "Order & Ranking"]
      },
      {
        name: "English Language",
        topics: ["Reading Comprehension", "Cloze Test", "Error Spotting", "Sentence Rearrangement", "Fill in the Blanks", "Vocabulary"]
      },
      {
        name: "General Awareness",
        topics: ["Banking Awareness", "Financial Awareness", "Current Affairs", "Static GK", "Indian Economy"]
      },
      {
        name: "Computer Knowledge",
        topics: ["Computer Fundamentals", "Networking", "Database", "MS Office", "Internet", "Computer Security"]
      }
    ]
  },
  {
    examName: "SBI PO",
    subjects: [
      {
        name: "Quantitative Aptitude",
        topics: ["Number Series", "Data Interpretation", "Quadratic Equations", "Simplification", "Percentage", "Ratio & Proportion", "Profit & Loss", "Time & Work", "Time & Distance", "Probability", "Permutation & Combination"]
      },
      {
        name: "Reasoning Ability",
        topics: ["Seating Arrangement", "Puzzles", "Syllogism", "Coding-Decoding", "Blood Relations", "Direction Sense", "Inequality", "Data Sufficiency", "Critical Reasoning", "Input-Output"]
      },
      {
        name: "English Language",
        topics: ["Reading Comprehension", "Cloze Test", "Error Spotting", "Sentence Rearrangement", "Para Jumbles", "Vocabulary", "Grammar"]
      },
      {
        name: "General Awareness",
        topics: ["Banking Awareness", "Financial Awareness", "Current Affairs", "Static GK", "Indian Economy", "Budget & Policy"]
      }
    ]
  },
  {
    examName: "UPSC Prelims",
    subjects: [
      {
        name: "Indian History",
        topics: ["Ancient India", "Medieval India", "Modern India", "Art & Culture", "Indian National Movement"]
      },
      {
        name: "Indian Polity",
        topics: ["Constitution", "Fundamental Rights", "DPSP", "Parliament", "Judiciary", "Federalism", "Local Government", "Constitutional Bodies", "Amendments"]
      },
      {
        name: "Geography",
        topics: ["Physical Geography", "Indian Geography", "World Geography", "Climatology", "Oceanography", "Environmental Geography"]
      },
      {
        name: "Economics",
        topics: ["Indian Economy", "Economic Planning", "Monetary Policy", "Fiscal Policy", "International Trade", "Banking & Finance", "Budget"]
      },
      {
        name: "General Science",
        topics: ["Physics", "Chemistry", "Biology", "Environment & Ecology", "Science & Technology"]
      },
      {
        name: "Current Affairs",
        topics: ["National Events", "International Events", "Awards & Honours", "Sports", "Government Schemes", "Summits & Conferences"]
      },
      {
        name: "CSAT",
        topics: ["Comprehension", "Logical Reasoning", "Analytical Ability", "Data Interpretation", "Decision Making", "Basic Numeracy"]
      }
    ]
  },
  {
    examName: "TSPSC Group-2",
    subjects: [
      {
        name: "General Studies Paper-1",
        topics: ["Indian History", "Indian Polity", "Indian Economy", "Geography", "General Science", "Disaster Management"]
      },
      {
        name: "General Studies Paper-2",
        topics: ["Telangana History", "Telangana Movement", "Telangana Economy", "Telangana Geography", "Telangana Culture", "Telangana Governance"]
      },
      {
        name: "General Studies Paper-3",
        topics: ["Indian Constitution", "Public Administration", "Good Governance", "Social Issues", "Current Affairs", "Science & Technology"]
      }
    ]
  },
  {
    examName: "RRB NTPC",
    subjects: [
      {
        name: "Mathematics",
        topics: ["Number System", "Algebra", "Percentage", "Ratio & Proportion", "Profit & Loss", "Time & Work", "Time & Distance", "SI & CI", "Mensuration", "Geometry", "Trigonometry", "Data Interpretation"]
      },
      {
        name: "General Intelligence & Reasoning",
        topics: ["Analogy", "Classification", "Series", "Coding-Decoding", "Blood Relations", "Direction Sense", "Syllogism", "Venn Diagram", "Statement & Conclusion", "Calendar & Clock"]
      },
      {
        name: "General Awareness",
        topics: ["Indian History", "Indian Polity", "Geography", "Economics", "General Science", "Current Affairs", "Static GK", "Computer Awareness"]
      }
    ]
  }
];

module.exports = exams;
