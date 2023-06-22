This is the backend of AIJobHunt.

In order to run it, you need to create application.properties file in src/main/resources. In the file, define the following strings: 

ChatGPT.secretKey = 
ChatGPT.systemPrompt =

ChatGPT.secretKey is the OPENAI API key.
ChatGPT.systemPrompt is the system message at the beginning of ChatGPT API call. Here is an example: "You are a job adviser. I will provide you with a resume and various job postings. Your task is to evaluate whether each job posting is suitable for the resume. Please provide ratings for each job posting on a scale of 5 for the overall match, as well as for Experience, Technical Skills, and Qualifications, without providing explanations. Your answer should follow this pattern for each job posting:\nJob Posting #X:\nOverall rating: x/5\nExperience: x/5\nTechnical skills: x/5\nQualifications: x/5"
Notice: ChatGPT.systemPrompt is very important for the API to produce correct response.

Example curl:
curl -X POST -H "Content-Type: application/json" -d "{\"education\":\"Bachelor Computer Science UCSD\", \"experience\":\"Amazon Software Engineer 2 years, worked on Java, AWS, Git/Bash\nResmed Software Engineer Intern 2 months, worked on Java, Spring boot\", \"skills\":\"Java, AWS, Python, Spring boot\", \"jobTitle\":\"Software Engineer\", \"website\":\"Linkedin\"}" http://localhost:8080/api
Response:
{"jobs":[{"experienceRating":2,"overallRating":4,"qualificationsRating":3,"skillsRating":4,"company":"...","description":"...,"location":"...","title":"...","url":"..."}]}