package cn.junechiu.firupload

import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by android on 2018/2/5.
 * 构建插件
 *  在控制台执行: gradlew build, 或者在 Gradle 面板执行 build 任务
 */
class FirUploadPlugin implements Plugin<Project> {

    private Project project  //项目对象

    @Override
    void apply(Project project) {
        this.project = project
        //接收外部参数  引用插件的工程中写：uploadArgs{appname='dd'}
        project.extensions.create('uploadArgs', FirUploadExtension)
        //引用task 依赖 assembleRelease
        project.task('uploadApk', type: UploadTask)
                .dependsOn('assembleRelease')
                .doLast {
            print("upload apk")
        }
    }
}
