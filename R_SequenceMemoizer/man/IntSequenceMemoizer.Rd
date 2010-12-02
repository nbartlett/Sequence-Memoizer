\name{IntSequenceMemoizer-class}
\alias{IntSequenceMemoizer-class}
\alias{IntSequenceMemoizer}
\alias{IntSequenceMemoizer,missing,missing,missing,missing,IntSequenceMemoizerParameters-method}
\alias{IntSequenceMemoizer,integer,missing,missing,missing,missing-method}
\alias{IntSequenceMemoizer,integer,integer,missing,missing,missing-method}
\alias{IntSequenceMemoizer,integer,integer,integer,integer,missing-method}

\title{IntSequenceMemoizer}
\description{
  Implementation of a sequence memoizer which takes int types.
  The constructor for \code{IntSequenceMemoizer} wraps the constructor for
  \code{\link{IntSequenceMemoizerParameters}} for convenience
}
\section{Extends}{
  Class \code{"\linkS4class{IntSequenceMemoizerInterface}"}.
}
\usage{
  IntSequenceMemoizer (alphabetSize, depth, maxNumberRestaurants, maxSequenceLength, parameters, \dots)
}
\arguments{
  \item{alphabetSize}{See \url{http://www.sequencememoizer.com/documentation/sequencememoizer/index.html?edu/columbia/stat/wood/pub/sequencememoizer/IntSequenceMemoizerParameters.html}}
  \item{depth}{See \url{http://www.sequencememoizer.com/documentation/sequencememoizer/index.html?edu/columbia/stat/wood/pub/sequencememoizer/IntSequenceMemoizerParameters.html}}
  \item{maxNumberRestaurants}{See \url{http://www.sequencememoizer.com/documentation/sequencememoizer/index.html?edu/columbia/stat/wood/pub/sequencememoizer/IntSequenceMemoizerParameters.html}}
  \item{maxSequenceLength}{See \url{http://www.sequencememoizer.com/documentation/sequencememoizer/index.html?edu/columbia/stat/wood/pub/sequencememoizer/IntSequenceMemoizerParameters.html}}
  \item{parameters}{Object of class \code{IntSequenceMemoizerParameters}}
  \item{\dots}{Not used.}
}
\references{
  \url{http://www.sequencememoizer.com/documentation/sequencememoizer/index.html?edu/columbia/stat/wood/pub/sequencememoizer/IntSequenceMemoizer.html}
}
