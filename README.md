## This is the backend of AIJobHunt.
# What is this
This is the backend service for website: [https://aijobhunt.xyz/](https://aijobhunt.xyz/). The backend provides a few API calls.


The first API call is [/api/submit]. The request contains the customer's resume. This API will retrieve a list of job postings that ChatGPT thinks your resume fits and return in response. The API first processes the resume in the request, then scrapes job postings with titles that the customer desires, combining all of the above data to form a prompt to call ChatGPT. After that, it processes the ChatGPT responses and returns the customer with job postings that best fit the resume.


The second API call is [/api/sendsuggestion]. This API is called when a customer writes a suggestion for the website. The API stores the text in an S3 bucket.


# How to run it
In order to run it, you need to create application.properties file in src/main/resources. In the file, define the following strings: 

ChatGPT.secretKey =

ChatGPT.systemPrompt =

ChatGPT.secretKey is the OPENAI API key.
ChatGPT.systemPrompt is the system message at the beginning of the ChatGPT API call. Here is an example: "You are a job adviser. I will provide you with a resume and various job postings. Your task is to evaluate whether each job posting is suitable for the resume. Please provide ratings for each job posting on a scale of 5 for the overall match, as well as for Experience, Technical Skills, and Qualifications, without providing explanations. Your answer should follow this pattern for each job posting:\nJob Posting #X:\nOverall rating: x/5\nExperience: x/5\nTechnical skills: x/5\nQualifications: x/5"
Notice: ChatGPT.systemPrompt is very important for the API to produce correct responses.

To use it, open [http://localhost:8080/#](http://localhost:8080/#).

# Other links
Frontend git: [https://github.com/FelixZhang1999/AIJobHuntClient]
