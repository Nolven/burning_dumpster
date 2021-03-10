function authenticationMiddleware () {
    return function (req, res, next) {
        if (req.isAuthenticated()) {
            return next()
        }
        console.log('not authenticated');
        res.redirect('/');
    }
}

module.exports = authenticationMiddleware;