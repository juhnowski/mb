function onloadCaptcha() {
    var sitekey = '6LfzEAsTAAAAAAARVvmZ1XFQ-34s9jcoMMV-gvvC';

    window.recaptchaCallback = grecaptcha.render('call-captcha', {
        'sitekey': sitekey
    });

    if ($('#contact-captcha').length > 0) {
        window.recaptchaContact = grecaptcha.render('contact-captcha', {
            'sitekey': sitekey
        });
    }
}
