import React from 'react';
import { StyleSheet, Text, View, NativeModules, Upload,  } from 'react-native';
debugger;
const reactnativeupdownloader = NativeModules.UploadModule;

const UploadManager = NativeModules.Upload;

export default class App extends React.Component {
  render() {
    return (
      <View style={styles.container}>
        <Text>Open up App.js to start working on your app!</Text>
        <Text>Changes you make will automatically reload.</Text>
        <Text>Shake your phone to open the developer menu.</Text>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
<<<<<<< HEAD
  }
=======
  },
>>>>>>> 5784f6b1ea01dba48339d15f6d642d6574a814ce
});
