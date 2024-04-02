# 更多资源qq810642693
# INTRODUCTION
此仓库为课程讲解中的代码和相关资料，欢迎下载代码，动手实践一下。如果在后面遇到了一些好的补充资料，我会上传到这个仓库中，大家也可以积极补充一下。
# INDEX

```
├── ch10
│   └── iris_pca
│       └── iris
│           └── src
├── ch11
│   └── sentiment_analysis
│       └── src
├── ch12
│   ├── ml-100k
│   └── rs
│       └── src
├── ch2 
├── ch3
├── ch4
│   └── src
├── ch6
├── ch7
│   ├── isotonic
│   │   └── isotonic
│   │       └── src
│   └── linear
│       └── src
├── ch8
│   ├── gender
│   │   └── src
│   └── iris
│       └── src
└── ch9
    ├── cluster_kmeans
        │   └── cluster
            │       └── src
                └── cluster_lda
                        └── cluster
                                    └── src
```

```
SocketDemo-L2
 * 1. 创建服务端socket对象，使用端口号2000，然后打印两行就绪状态信息，阻塞等待，直到有客户端连接。
 * 2. 循环等待客户端连接后，以socket对象作为参数，构造客户端Handler异步线程对象，并启动之。
 *    a) 客户端Handler线程从socket逐行读入流，根据读入内容为"bye"时回送原文给客户端，并结束运行，否则
 *    b) 回送读到字符串长度给客户端后等待接收下一行新数据流。
```

# TIPS
1. 源码中剔除.idea相关配置文件，这些是idea的配置文件，如果大家有喜欢使用eclipse的也可以使用其导入;
2. To be continued

# REFERENCE
1. 统计学习方法,李航(算法书)
2. Spark快速大数据分析(入门书)
3. 集体智慧编程(推荐系统)
