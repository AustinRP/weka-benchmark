# CSCE 569 Data Munger/Cruncher Program
# Copyright (c) Philip Conrad, 2015. All rights reserved.
#


# -------------------------------------------------------------------
# Serial
# -------------------------------------------------------------------

# Read in serial files.
serial_128x128    = read.csv("serial_128x128.csv")
serial_256x256    = read.csv("serial_256x256.csv")
serial_512x512    = read.csv("serial_512x512.csv")
serial_1024x1024  = read.csv("serial_1024x1024.csv")
serial_2048x2048  = read.csv("serial_2048x2048.csv")
serial_4096x4096  = read.csv("serial_4096x4096.csv")


# Merge the data frames together.
serial_results <- Reduce(function(x, y) merge(x, y, by=c("NumThreads","Method")), list(
  serial_128x128,
  serial_256x256,
  serial_512x512,
  serial_1024x1024,
  serial_2048x2048,
  serial_4096x4096))

# Assign pretty names to the columns.
colnames(serial_results) = c('NumThreads', 'Method', '128x128', '256x256', '512x512', '1024x1024', '2048x2048', '4096x4096')


# -------------------------------------------------------------------
# Parallel-Simple
# -------------------------------------------------------------------

# Read in parallel-simple files.
parallel_simple_128x128    = read.csv("parallel-simple_128x128.csv")
parallel_simple_256x256    = read.csv("parallel-simple_256x256.csv")
parallel_simple_512x512    = read.csv("parallel-simple_512x512.csv")
parallel_simple_1024x1024  = read.csv("parallel-simple_1024x1024.csv")
parallel_simple_2048x2048  = read.csv("parallel-simple_2048x2048.csv")
parallel_simple_4096x4096  = read.csv("parallel-simple_4096x4096.csv")


# Merge the data frames together.
parallel_simple_results <- Reduce(function(x, y) merge(x, y, by=c("NumThreads","Method")), list(
  parallel_simple_128x128,
  parallel_simple_256x256,
  parallel_simple_512x512,
  parallel_simple_1024x1024,
  parallel_simple_2048x2048,
  parallel_simple_4096x4096))

# Assign pretty names to the columns.
colnames(parallel_simple_results) = c('NumThreads', 'Method', '128x128', '256x256', '512x512', '1024x1024', '2048x2048', '4096x4096')


# -------------------------------------------------------------------
# Parallel-Smart
# -------------------------------------------------------------------

# Read in parallel-smart files.
parallel_smart_128x128    = read.csv("parallel-smart_128x128.csv")
parallel_smart_256x256    = read.csv("parallel-smart_256x256.csv")
parallel_smart_512x512    = read.csv("parallel-smart_512x512.csv")
parallel_smart_1024x1024  = read.csv("parallel-smart_1024x1024.csv")
parallel_smart_2048x2048  = read.csv("parallel-smart_2048x2048.csv")
parallel_smart_4096x4096  = read.csv("parallel-smart_4096x4096.csv")


# Merge the data frames together.
parallel_smart_results <- Reduce(function(x, y) merge(x, y, by=c("NumThreads","Method")), list(
  parallel_smart_128x128,
  parallel_smart_256x256,
  parallel_smart_512x512,
  parallel_smart_1024x1024,
  parallel_smart_2048x2048,
  parallel_smart_4096x4096))

# Assign pretty names to the columns.
colnames(parallel_smart_results) = c('NumThreads', 'Method', '128x128', '256x256', '512x512', '1024x1024', '2048x2048', '4096x4096')


# -------------------------------------------------------------------
# Output
# -------------------------------------------------------------------

write.csv(serial_results, "serial-results.csv")
write.csv(parallel_simple_results, "parallel-simple-results.csv")
write.csv(parallel_smart_results, "parallel-smart-results.csv")



