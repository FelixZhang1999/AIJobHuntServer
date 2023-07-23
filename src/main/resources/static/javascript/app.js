$(document).ready(function() {
    var lastTitle = "";
    var lastLocation = "";
    var nextStart = 0;
    var jobLists = [];

    // Add component button click event
    $('#education-add-button').click(function() {
        var component = $('.app-component-container1:first').clone(); // Clone the first form component
        component.find('input').val(''); // Clear input field in the cloned component
        component.appendTo('#education-container'); // Append the cloned component to the container
    });

    // Remove component button click event
    $('#education-container').on('click', '.education-remove-button', function() {
        if ($('.education-remove-button').length > 1) {
            $(this).parent('.app-component-container1').remove(); // Remove the parent form component
        }
    });

    // Add component button click event
    $('#experience-add-button').click(function() {
        var component = $('.component1-container1:first').clone(); // Clone the first form component
        component.find('input').val(''); // Clear input field in the cloned component
        component.find('textarea').val('');
        component.appendTo('#experience-container'); // Append the cloned component to the container
    });

    // Remove component button click event
    $('#experience-container').on('click', '.experience-remove-button', function() {
        if ($('.experience-remove-button').length > 1) {
            $(this).parent('.component1-container1').remove(); // Remove the parent form component
        }
    });

    // Form submission event
    $('.home-button1').click(function(e) {
        e.preventDefault(); // Prevent the default form submission behavior

        if ($('#resume-container').find('input[name="desiredTitle"]').val().length == 0) {
            $('.home-error1').text("This field is required!");
            return;
        } else {
            $('.home-error1').text("");
        }

        $(this).prop("disabled", true);
        $(this).text("Please wait...");
        $('#error-message').text("");

        var formData = {};
        if (lastTitle ===  $('#resume-container').find('input[name="desiredTitle"]').val() &&
            lastLocation === $('#resume-container').find('input[name="location"]').val()) {
            if (jobLists.length > 0) {
                setTimeout(function(){
                    fillJobData();
                    $('.home-button1').prop("disabled", false);
                    $('.home-button1').text("Search");
                }, 1000);
                return;
            }
            formData['nextStart'] = nextStart;
        }else{
            formData['nextStart'] = 0;
            nextStart = 0;
        }

        var education = [];
        $('#resume-container').find('.app-component-container1').each(function(index) {
            var school = $(this).find('input[name="school"]').val();
            var major = $(this).find('input[name="major"]').val();
            var degree = $(this).find('select[name="degree"]').val();
            if (school.length == 0 && major.length == 0) {
                return;
            }
            var graduated = $(this).find('input[name="graduated"]').prop('checked');
            education.push({
                school: school,
                major: major,
                graduated: graduated,
                degree: degree
            });
        });
        formData['education'] = education;

        var experience = [];
        $('#resume-container').find('.component1-container1').each(function(index) {
            var title = $(this).find('input[name="title"]').val();
            var duration = $(this).find('input[name="duration"]').val();
            var description = $(this).find('textarea[name="description"]').val();
            var company = $(this).find('input[name="company"]').val();
            if (title.length == 0 && duration.length == 0 && company.length == 0 && description.length == 0) {
                return;
            }
            experience.push({
                title: title,
                duration: duration,
                description: description,
                company: company
            });
        });
        formData['experience'] = experience;
        formData['desiredTitle'] = $('#resume-container').find('input[name="desiredTitle"]').val();
        formData['location'] = $('#resume-container').find('input[name="location"]').val();
        formData['website'] = $('#resume-container').find('select[name="website"]').val();

        lastTitle = formData['desiredTitle'];
        lastLocation = formData['location'];

        console.log();
        console.log("Form Data: " + JSON.stringify(formData));

        // Make AJAX POST request to submit the form data
        $.ajax({
            url: '/submit',
            type: 'POST',
            data: JSON.stringify(formData),
            contentType: 'application/json',
            success: function(response) {
                // Display the result dynamically
                console.log(response);
                if (response.error == true) {
                    $('#error-message').text(response.message);
                } else {
                    jobLists = response["jobs"];
                    fillJobData();
                }
                nextStart = response["nextStart"];
                $('.home-button1').prop("disabled", false);
                $('.home-button1').text("Search");
            },
            error: function(response) {
                $('#error-message').text("Something is wrong. Please try again.");
                $('.home-button1').prop("disabled", false);
                $('.home-button1').text("Search");
            }
        });
    });

    function fillJobData() {
        var job = jobLists[0];
        $('#result-title').text(job["title"]);
        $('#result-company').text(job["company"]);
        $('#result-location').text(job["location"]);
        $('#result-url').text("Link");
        $('#result-url').attr("href", job["url"]);
        jobLists.shift();
        console.log("Has " + jobLists.length + " left.");
    }
});