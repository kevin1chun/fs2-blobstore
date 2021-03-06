/*
Copyright 2018 LendUp Global, Inc.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package blobstore
package fs

import java.nio.file.{Files, Paths, Path => NioPath}
import scala.util.control.NonFatal
import cats.effect.IO

class FileStoreTest extends AbstractStoreTest {

  val rootDir: NioPath = Paths.get("tmp/file-store-root/")
  override val store: Store[IO] = FileStore[IO](rootDir)
  override val root: String = "file_tests"

  behavior of "FileStore.put"
  it should "not have side effects when creating a Sink" in {
    store.put(Path(s"fs_tests_$testRun/path/file.txt"))
    store.list(Path(s"fs_tests_$testRun/")).compile.toList.unsafeRunSync().isEmpty must be(true)
  }

  // remove dirs created by AbstractStoreTest
  override def afterAll(): Unit = {
    super.afterAll()

    val clean = List("all", "list-many", "move-keys", "list-dirs/subdir", "list-dirs",
      "transfer-dir-to-dir-dst", "transfer-file-to-file-dst", "transfer-single-file-to-dir-dst",
      "transfer-dir-rec-dst/subdir/", "transfer-dir-rec-dst")
      .map(t => rootDir.resolve(s"$root/test-$testRun/$t")) ++
      List(rootDir.resolve(s"$root/test-$testRun"), rootDir.resolve(s"$root"), rootDir)

    clean.foreach(p => try { Files.delete(p) } catch { case NonFatal(_) => /* noop */ })
  }

}