# react-native-alpha-video

## Getting started

`$ npm install react-native-alpha-video --save`

## Usage
```javascript

import React, { useEffect, useState } from 'react';
import {
  Button,
  Dimensions,
  Modal,
  SafeAreaView,
  StatusBar,
  StyleSheet,
  Text,
  useColorScheme,
  View,
} from 'react-native';
import { AlphaVideoModule, AlphaVideoView } from 'react-native-alpha-video';

import {
  Colors,
} from 'react-native/Libraries/NewAppScreen';

const svgaList_net = [
  'https://github.com/JedShiMing/asstes/blob/master/demo_video.mp4?raw=true',
  'https://github.com/JedShiMing/asstes/blob/master/plane_x264.mp4?raw=true',
  'https://github.com/JedShiMing/asstes/blob/master/wheel.mp4?raw=true'
]

const svgaList_native = [
  AlphaVideoModule.getAssets(require('./assets/wwww.mp4'))
]

const { width, height } = Dimensions.get('window')
const kScreenW = width
const kScreenH = height

let alphaVideoRef: AlphaVideoView | null

const App = () => {

  const [url, setUrl] = useState('')
  const [modal, setModal] = useState(false)

  const isDarkMode = useColorScheme() === 'dark';

  const backgroundStyle = {
    backgroundColor: isDarkMode ? Colors.darker : Colors.lighter,
    flex: 1,
  };

  function load(url: any) {
    alphaVideoRef?.clear()
    setUrl(url)
    setModal(true)
  }

  return (
    <SafeAreaView style={backgroundStyle}>
      <StatusBar barStyle={isDarkMode ? 'light-content' : 'dark-content'} />
      <Text style={{ alignSelf: 'center', fontSize: 30 }}>预加载需要科学上网</Text>
      <Text style={{ alignSelf: 'center', fontSize: 30 }}>本地资源最好用release包测试</Text>
      <View style={styles.btnView}>
        <Button title={'预加载全部'} onPress={() => {
          // 预加载
          AlphaVideoModule.advanceDownload([...svgaList_net, ...svgaList_native])
        }} />
        <Button title={'网络1'} onPress={() => {
          // 预加载
          load(svgaList_net[0])
        }} />
        <Button title={'网络2'} onPress={() => {
          // 预加载
          load(svgaList_net[1])
        }} />
        <Button title={'网络3'} onPress={() => {
          // 预加载
          load(svgaList_net[2])
        }} />
        <Button title={'本地1'} onPress={() => {
          // 预加载
          load(svgaList_native[0])
        }} />
      </View>

      <Modal transparent={true} visible={modal} >
        <View style={{ backgroundColor: 'rgba(0,0,0,0.5)', flex: 1 }}>
          {/* 用 modal && 是为了销毁svgaview */}
          {
            modal && <AlphaVideoView
              ref={ref => (alphaVideoRef = ref)}
              source={url}
              loop={false}
              onDidPlayFinish={() => {
                console.log("播放完毕");
                setModal(false)
              }}
            />
          }
        </View>


      </Modal>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  btnView: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    flexWrap: 'wrap',
    position: 'absolute',
    bottom: 50,
    width: '100%',
  },
  button1: {
    width: 100,
    height: 40,
    backgroundColor: 'white',
    borderRadius: 10,
    justifyContent: 'center',
    alignItems: 'center',
    alignSelf: 'center'
  }
});

export default App;

```
## 注意点
iOS使用需要在项目中创建一个metal文件`filter.metal`文件内容
![image](https://gitee.com/ZLforever/source/raw/master/image1.png)
```javascript
#include <metal_stdlib>
using namespace metal;
#include <CoreImage/CoreImage.h>

extern "C" { namespace  coreimage {
    float4 maskVideoMetal(sample_t s,sample_t m){
        return float4(s.rgb,m.r);
        }
    }
}

```
然后在Build Setting中搜索meta,如下
![image](https://gitee.com/JedShi/asstes-clone/raw/master/pic/image.png)