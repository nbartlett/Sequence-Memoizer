.onLoad <- function (libname, pkgname) {
	if(!require(rJava)) stop("RSM needs rJava")
	.jpackage(pkgname);	
}

.as.integer <- function (x, stop.if.not.integer=TRUE, ...) {
	if (class (x) == "numeric") {
		warning (paste ("coercing ", match.call()$x, " into an integer.", sep="'"))
		x <- as.integer (x, ...)
	}
	if (stop.if.not.integer==TRUE && class (x) != "integer") {
		stop ("expecting ", match.call()$x, " to be integer.", sep="'")
	}
	return (x)
}