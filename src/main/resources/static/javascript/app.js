$(document).ready(function() {
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
    $('.submit-button').click(function(e) {
        e.preventDefault(); // Prevent the default form submission behavior

        var formData = {};
        var education = [];
        $('#resume-container').find('.app-component-container1').each(function(index) {
            var school = $(this).find('input[name="school"]').val();
            var major = $(this).find('input[name="major"]').val();
            var graduated = $(this).find('input[name="graduated"]').prop('checked');
            var degree = $(this).find('select[name="degree"]').val();
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
            experience.push({
                title: title,
                duration: duration,
                description: description,
                company: company
            });
        });
        formData['experience'] = experience;
        formData['desiredTitle'] = $('#resume-container').find('input[name="desiredTitle"]').val();
        formData['website'] = $('#resume-container').find('select[name="website"]').val();

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
                var job = response.jobs[0];
                $('#result-title').text(job["title"]);
                $('#result-company').text(job["company"]);
                $('#result-location').text(job["location"]);
                $('#result-url').text("Link");
                $('#result-url').attr("href", job["url"]);
                console.log(response);
            }
        });
    });
});