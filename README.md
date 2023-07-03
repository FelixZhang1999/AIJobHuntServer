This is the backend of AIJobHunt.

In order to run it, you need to create application.properties file in src/main/resources. In the file, define the following strings: 

ChatGPT.secretKey =
ChatGPT.systemPrompt =

ChatGPT.secretKey is the OPENAI API key.
ChatGPT.systemPrompt is the system message at the beginning of ChatGPT API call. Here is an example: "You are a job adviser. I will provide you with a resume and various job postings. Your task is to evaluate whether each job posting is suitable for the resume. Please provide ratings for each job posting on a scale of 5 for the overall match, as well as for Experience, Technical Skills, and Qualifications, without providing explanations. Your answer should follow this pattern for each job posting:\nJob Posting #X:\nOverall rating: x/5\nExperience: x/5\nTechnical skills: x/5\nQualifications: x/5"
Notice: ChatGPT.systemPrompt is very important for the API to produce correct response.

To use it, open [http://localhost:8080/#](http://localhost:8080/#).