#  An infodemic case study dataset

Tweets retrieved from the George Washington University’s publicly available dataset called Tweetsets.
Existing tweets where a user shares a tweet, that means that the user has been influenced by the original tweet, are used to build this instance. 
The tweetset used is related to infodemics in the area of Healthcare, related to the announcement of the American Health Care Act (AHCA) in 2017.
This dataset consists of 386384 tweets, where 284131 are retweets. 

The original dataset contains all the identifiers of the tweets and, in order to generate this instance, we have retrieved it from Twitter, resulting in 96705 tweets. Notice
that 187426 tweets have been removed from Twitter due to fake news filters or suspended accounts. 

The final dataset has 54836 users, 96705 tweets, and 2060 components, where the largest component has 47257 nodes.

## Data

- dataset_twitter.txt contains the social network with nodes and edges.
- dataset_twitter_match.txt contains the associated twitter account to each number.