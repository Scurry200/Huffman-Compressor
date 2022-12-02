calgary (store count)
total percent compression 20.659

calgary (store tree)
total percent compression 16.797

books (store count)
total percent compression 40.460

books (store tree)
total percent compression 25.9881

waterloo (store count)
total percent compression 10.599

waterloo (store tree)
total percent compression 18.181


Huffman is great with large (and especially text-based) files because there's more chances for
repetition of ascii characters that can simply be just stored in an array as a frequency if there's
an ascii value in the file. It's very efficient and saves a substantial amount of space.
However, it's not great with smaller files (because there's not much to compress when typically
there's only 1-2 times a character is repeated, if at all) or non-text files, making it pretty
useless to go through the huffman compressing process. For non-text files, there's more variations in character values,
so less compression. When you try to compress a huffman file, most of the data is compressed as much as it
can (with the huffman process), so it may have some benefit and compress slightly further but typically it's
not very big of a difference from the regular huff file. It'll eventually lead to expanding the huffman file bigger
than what it originally was, which is counterintuitive and pointless.
