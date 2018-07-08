# Plex Subtitle Search

This is a demonstration of a subtitle browser that could be
implemented within Plex. A user could use the subtitle browser to
search for and find specific scenes of dialogue and play the media
file at that exact moment.

## Overview

Using the existing subtitle information and generated thumbnails from
Plex, a searchable database and subtitle browser with matching
thumbnails can be created.

The generated thumbnails have already been extracted from the BIF file
for the purposes of this demo in `resources/thumbnails`.

## Running

Replace the `resources/thumbnails` directory with extracted images
from a BIF file, replace the subtitle file in `resources/subtitle.srt`
and provide the runtime of the media file as the argument to the
script below:

```Clojure
clj -m subtitle-search-poc.core "00:44:49,000"
```

## Viewing

The compiled demo is located at `resources/index.html`.
